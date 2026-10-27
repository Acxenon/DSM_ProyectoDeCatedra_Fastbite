package com.example.fastbite.data

import android.content.Context
import com.example.fastbite.model.FoodOrder
import com.example.fastbite.model.OrderLine
import com.example.fastbite.domain.SalvadorPhone
import com.example.fastbite.domain.OrderPricing
import com.example.fastbite.domain.OrderTransitions
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** Datos locales aislados por UID; los pedidos se sincronizan con users/UID/orders. */
class FastBiteStore(context: Context, private val uid: String) {
    private val preferences = context.getSharedPreferences("fastbite_$uid", Context.MODE_PRIVATE)
    val cart = linkedMapOf<String, Int>()
    val favorites = preferences.getStringSet("favorites", emptySet())!!.toMutableSet()
    val orders = mutableListOf<FoodOrder>()
    private val sending = mutableSetOf<String>()
    private val cloud = FirestoreOrdersDataSource(uid)
    var syncMessage = "Los pedidos se guardan en este dispositivo."
        private set
    var onChanged: (() -> Unit)? = null
    var address: String
        get() = preferences.getString("address", "").orEmpty()
        set(value) { preferences.edit().putString("address", value).apply() }
    var phone: String
        get() = preferences.getString("phone", "").orEmpty()
        set(value) { preferences.edit().putString("phone", value).apply() }

    init {
        val savedCart = runCatching { JSONObject(preferences.getString("cart", "{}")!!) }.getOrElse { JSONObject() }
        savedCart.keys().forEach { key ->
            val quantity = savedCart.optInt(key)
            if (FoodCatalog.find(key) != null && quantity in 1..20) cart[key] = quantity
        }
        val savedOrders = runCatching { JSONArray(preferences.getString("orders", "[]")!!) }.getOrElse { JSONArray() }
        for (i in 0 until savedOrders.length()) {
            runCatching { FoodOrder.from(savedOrders.getJSONObject(i)) }.getOrNull()?.let {
                if (it.ownerId == uid) orders.add(it)
            }
        }
    }
    fun persistCart() { preferences.edit().putString("cart", JSONObject(cart.toMap()).toString()).apply() }
    private fun persistOrders(): Boolean = preferences.edit()
        .putString("orders", JSONArray().apply { orders.forEach { put(it.json()) } }.toString()).commit()
    fun add(id: String, quantity: Int = 1): Boolean {
        if (FoodCatalog.find(id) == null || quantity < 1 || (cart[id] ?: 0) + quantity > 20) return false
        cart[id] = (cart[id] ?: 0) + quantity
        persistCart()
        return true
    }
    fun quantity(id: String, value: Int) {
        if (value <= 0) cart.remove(id) else if (FoodCatalog.find(id) != null) cart[id] = value.coerceAtMost(20)
        persistCart()
    }
    fun clearCart() { cart.clear(); persistCart() }
    fun toggleFavorite(id: String) {
        if (!favorites.remove(id)) favorites.add(id)
        preferences.edit().putStringSet("favorites", favorites.toSet()).apply()
    }
    fun lines() = cart.mapNotNull { (id, qty) -> FoodCatalog.find(id)?.let { OrderLine(id, it.name, it.emoji, it.price, qty) } }
    fun subtotal(): Int { val lines = lines(); return OrderPricing.subtotal(lines.map { it.price }.toIntArray(), lines.map { it.quantity }.toIntArray()) }
    fun couponAvailable() = orders.none { it.status != "CANCELLED" }
    fun create(customer: String, address: String, phone: String, notes: String, coupon: Boolean): FoodOrder {
        check(cart.isNotEmpty()) { "El carrito está vacío" }
        check(address.isNotBlank()) { "Faltan los datos de entrega" }
        check(SalvadorPhone.isValid(phone)) { "El teléfono debe tener exactamente 8 dígitos" }
        check(!coupon || couponAvailable()) { "El cupón solo aplica al primer pedido" }
        val now = System.currentTimeMillis()
        val subtotal = subtotal()
        val order = FoodOrder(UUID.randomUUID().toString(), uid, customer, lines(), address, phone, notes,
            subtotal, OrderPricing.discount(subtotal, coupon), OrderPricing.delivery(subtotal), OrderPricing.total(subtotal, coupon), now, now)
        orders.add(0, order)
        // Guardar pedido y vaciar el carrito en una sola escritura evita duplicados tras reiniciar.
        val saved = preferences.edit()
            .putString("orders", JSONArray().apply { orders.forEach { put(it.json()) } }.toString())
            .putString("cart", "{}")
            .putString("address", address).putString("phone", phone).commit()
        if (!saved) { orders.remove(order); error("No se pudo guardar el pedido en el dispositivo") }
        cart.clear()
        syncPending()
        return order
    }
    fun cancel(id: String): Boolean = changeStatus(id, "CANCELLED")

    fun markDelivered(id: String): Boolean = changeStatus(id, "DELIVERED")

    private fun changeStatus(id: String, target: String): Boolean {
        val order = orders.find { it.id == id } ?: return false
        if (!OrderTransitions.canChange(order.status, target)) return false
        val oldStatus = order.status
        val oldTime = order.updatedAt
        val oldSync = order.synced
        order.status = target
        order.updatedAt = maxOf(System.currentTimeMillis(), oldTime + 1)
        order.synced = false
        if (!persistOrders()) {
            order.status = oldStatus
            order.updatedAt = oldTime
            order.synced = oldSync
            return false
        }
        syncPending()
        return true
    }
    fun refresh() {
        syncPending()
        cloud.loadRecent()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { document ->
                    val remote = runCatching { FoodOrder.from(JSONObject(document.data ?: emptyMap<String, Any>())) }.getOrNull()
                    if (remote != null && remote.ownerId == uid) {
                        val local = orders.find { it.id == remote.id }
                        if (local == null || (remote.updatedAt >= local.updatedAt && (local.synced || remote.status in listOf("CONFIRMED", "DELIVERED", "CANCELLED")))) {
                            if (local != null) orders.remove(local)
                            remote.synced = !document.metadata.hasPendingWrites()
                            orders.add(remote)
                        }
                    }
                }
                orders.sortByDescending { it.createdAt }
                persistOrders()
                syncMessage = if (snapshot.metadata.isFromCache) "Sin conexión: mostrando historial guardado." else if (orders.any { !it.synced }) "Hay pedidos pendientes de sincronizar." else "Historial sincronizado con Firebase."
                onChanged?.invoke()
            }
            .addOnFailureListener {
                syncMessage = "Historial local. Revisa tu conexión y la configuración de Firestore."
                onChanged?.invoke()
            }
    }
    private fun syncPending() {
        orders.filter { !it.synced && it.id !in sending }.forEach { order ->
            val version = order.updatedAt
            sending.add(order.id)
            cloud.save(order)
                .addOnSuccessListener {
                    sending.remove(order.id)
                    if (order.updatedAt == version) { order.synced = true; persistOrders() }
                    syncMessage = if (orders.any { !it.synced }) "Hay pedidos pendientes de sincronizar." else "Historial sincronizado con Firebase."
                    onChanged?.invoke()
                    if (order.updatedAt != version) syncPending()
                }
                .addOnFailureListener {
                    sending.remove(order.id)
                    syncMessage = "Pedido guardado aquí; pendiente de sincronizar. Revisa Firestore y tu conexión."
                    onChanged?.invoke()
                }
        }
    }
}
