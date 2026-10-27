package com.example.fastbite.ui.screens

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.fastbite.MainActivity
import com.example.fastbite.R
import com.example.fastbite.data.FoodCatalog
import com.example.fastbite.domain.OrderTransitions
import com.example.fastbite.domain.SalvadorPhone
import com.example.fastbite.model.FoodOrder
import com.example.fastbite.ui.UiFormat
import com.example.fastbite.ui.onTextChanged
import java.util.Locale

class OrdersScreen(host: MainActivity) : Screen(host, R.layout.screen_orders) {
    private val list: LinearLayout = find(R.id.orders_list)

    init {
        find<Button>(R.id.orders_refresh).setOnClickListener { host.toast("Actualizando historial…"); store.refresh() }
        find<EditText>(R.id.orders_search).apply {
            setText(state.orderQuery)
            onTextChanged { state.orderQuery = it; populate() }
        }
        components.chips(find(R.id.orders_filters), listOf("Todos", "Pendientes", "Entregados", "Cancelados"), state.orderFilter) {
            state.orderFilter = it
            host.hideKeyboard()
            host.refreshScreen()
        }
        onStoreChanged()
    }

    override fun onStoreChanged() {
        find<TextView>(R.id.orders_sync).text = store.syncMessage
        populate()
    }

    private fun populate() {
        list.removeAllViews()
        val query = FoodCatalog.normalized(state.orderQuery.trim())
        val orders = store.orders.filter { order ->
            val matchesStatus = when (state.orderFilter) {
                "Pendientes" -> order.status in listOf("PENDING", "CONFIRMED")
                "Entregados" -> order.status == "DELIVERED"
                "Cancelados" -> order.status == "CANCELLED"
                else -> true
            }
            matchesStatus && FoodCatalog.normalized("${order.id} ${UiFormat.date(order.createdAt)} ${order.lines.joinToString { it.name }}").contains(query)
        }.sortedByDescending { it.createdAt }
        if (orders.isEmpty()) {
            components.empty(list, "No hay pedidos para mostrar", if (store.orders.isEmpty()) "Tu próximo pedido aparecerá aquí. Explora el menú para comenzar." else "Prueba otro filtro o búsqueda.",
                if (store.orders.isEmpty()) ({ host.showTab("Buscar") }) else null)
        }
        orders.forEach { bindOrder(it) }
    }

    private fun bindOrder(order: FoodOrder) {
        val item = host.layoutInflater.inflate(R.layout.item_order, list, false)
        item.findViewById<TextView>(R.id.order_number).text = "Pedido #${order.id.take(8).uppercase(Locale.ROOT)}"
        item.findViewById<TextView>(R.id.order_status).text = UiFormat.status(order.status)
        item.findViewById<TextView>(R.id.order_date).text = UiFormat.date(order.createdAt)
        item.findViewById<TextView>(R.id.order_items).text = order.lines.joinToString("\n") { "${it.quantity} × ${it.name}" }
        item.findViewById<TextView>(R.id.order_sync).text = if (order.synced) "✓ Sincronizado" else "Guardado aquí · pendiente de sincronizar"
        item.findViewById<TextView>(R.id.order_total).text = "Total ${UiFormat.money(order.total)}"
        item.findViewById<Button>(R.id.order_details).setOnClickListener { details(order) }
        item.findViewById<Button>(R.id.order_repeat).setOnClickListener { repeat(order) }
        item.findViewById<Button>(R.id.order_delivered).apply {
            visibility = if (OrderTransitions.canChange(order.status, "DELIVERED")) View.VISIBLE else View.GONE
            setOnClickListener {
                host.confirm("Marcar como entregado", "¿Confirmas que recibiste este pedido?") {
                    if (store.markDelivered(order.id)) {
                        state.orderFilter = "Entregados"
                        state.orderQuery = ""
                        host.toast("Pedido marcado como entregado")
                        host.refreshScreen()
                    } else host.toast("No se pudo actualizar el pedido. Intenta de nuevo.")
                }
            }
        }
        item.findViewById<Button>(R.id.order_cancel).apply {
            visibility = if (order.status == "PENDING") View.VISIBLE else View.GONE
            setOnClickListener {
                host.confirm("Cancelar pedido", "¿Cancelar este pedido pendiente?") {
                    if (store.cancel(order.id)) { host.toast("Pedido cancelado"); populate() }
                    else host.toast("No se pudo cancelar el pedido")
                }
            }
        }
        list.addView(item)
    }

    private fun details(order: FoodOrder) {
        val lines = order.lines.joinToString("\n") { "${it.quantity} × ${it.name}  ${UiFormat.money(it.price * it.quantity)}" }
        val message = "${UiFormat.date(order.createdAt)}\nEstado: ${UiFormat.status(order.status)}\n\n$lines\n\nSubtotal: ${UiFormat.money(order.subtotal)}\nDescuento: ${UiFormat.money(order.discount)}\nEnvío: ${UiFormat.money(order.delivery)}\nTotal: ${UiFormat.money(order.total)}\n\nCliente: ${order.customer}\nEntrega: ${order.address}\nTeléfono: ${SalvadorPhone.display(order.phone)}\nPago: efectivo al recibir" +
            (if (order.notes.isBlank()) "" else "\nNotas: ${order.notes}") +
            (if (order.status == "PENDING") "\n\nPendiente de confirmación del restaurante." else "")
        host.info("Pedido #${order.id.take(8).uppercase(Locale.ROOT)}", message)
    }

    private fun repeat(order: FoodOrder) {
        host.confirm("Repetir pedido", "Se agregarán estas comidas al carrito actual con los precios del menú de hoy.") {
            var skipped = 0
            order.lines.forEach { line ->
                if (FoodCatalog.find(line.foodId) == null) skipped++
                else {
                    val quantity = minOf(line.quantity, 20 - (store.cart[line.foodId] ?: 0))
                    if (quantity > 0) store.add(line.foodId, quantity)
                    if (quantity < line.quantity) skipped++
                }
            }
            if (skipped > 0) host.toast("Algunas cantidades no se añadieron: máximo 20 por comida")
            host.showCart()
        }
    }
}
