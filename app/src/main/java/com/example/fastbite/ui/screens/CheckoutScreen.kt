package com.example.fastbite.ui.screens

import android.widget.Button
import android.widget.EditText
import com.example.fastbite.MainActivity
import com.example.fastbite.R
import com.example.fastbite.domain.SalvadorPhone
import com.example.fastbite.domain.OrderPricing
import com.example.fastbite.ui.UiFormat
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class CheckoutScreen(host: MainActivity) : Screen(host, R.layout.screen_checkout) {
    private val address: EditText = find(R.id.checkout_address)
    private val phone: EditText = find(R.id.checkout_phone)
    private val notes: EditText = find(R.id.checkout_notes)

    init {
        address.setText(state.draftAddress ?: store.address)
        phone.setText(SalvadorPhone.forEditing(state.draftPhone ?: store.phone))
        notes.setText(state.draftNotes)
        components.cartLines(find(R.id.checkout_lines)) { host.refreshScreen() }
        find<Button>(R.id.checkout_confirm).setOnClickListener { placeOrder() }
        updateTotals()
    }

    override fun captureState() {
        state.draftAddress = address.text.toString()
        state.draftPhone = phone.text.toString()
        state.draftNotes = notes.text.toString()
    }

    override fun onStoreChanged() { updateTotals() }

    private fun updateTotals() {
        components.summary(find(R.id.checkout_summary), false)
        find<Button>(R.id.checkout_confirm).text = "Confirmar pedido · ${UiFormat.money(OrderPricing.total(store.subtotal(), false))}"
    }

    private fun placeOrder() {
        captureState()
        val deliveryAddress = state.draftAddress.orEmpty().trim()
        val contactPhone = state.draftPhone.orEmpty().trim()
        if (deliveryAddress.length < 8) { address.error = "Escribe una dirección completa"; address.requestFocus(); return }
        if (!SalvadorPhone.isValid(contactPhone)) { phone.error = "El teléfono debe tener exactamente 8 dígitos"; phone.requestFocus(); return }
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) { host.goToLogin(); return }
        val customer = user.displayName.orEmpty().ifBlank { user.email.orEmpty() }
        host.confirm("Confirmar pedido", "Total: ${UiFormat.money(OrderPricing.total(store.subtotal(), false))}\nPago en efectivo al recibir.\n\nEntrega: $deliveryAddress") {
            try {
                val order = store.create(customer, deliveryAddress, contactPhone, state.draftNotes.trim(), false)
                state.orderFilter = "Todos"
                state.orderQuery = ""
                host.showTab("Pedidos")
                state.draftAddress = null
                state.draftPhone = null
                state.draftNotes = ""
                host.info("Pedido guardado · ${order.id.take(8).uppercase(Locale.ROOT)}", "Tu pedido está pendiente de confirmación del restaurante. Consulta sus detalles en Pedidos.\n\nLa app indica si está sincronizado o guardado solo en este dispositivo.")
            } catch (e: Exception) { host.toast("No se pudo guardar el pedido. Intenta de nuevo.") }
        }
    }
}
