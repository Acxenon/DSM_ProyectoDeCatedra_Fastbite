package com.example.fastbite.ui.screens

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.example.fastbite.MainActivity
import com.example.fastbite.R

class CartScreen(host: MainActivity) : Screen(host, R.layout.screen_cart) {
    init {
        val empty = store.cart.isEmpty()
        val lines: LinearLayout = find(R.id.cart_lines)
        if (empty) components.empty(lines, "Tu carrito está vacío", "Agrega comidas desde Inicio o Buscar para comenzar.")
        else components.cartLines(lines) { host.refreshScreen() }
        find<View>(R.id.cart_summary).apply {
            visibility = if (empty) View.GONE else View.VISIBLE
            if (!empty) components.summary(this, false)
        }
        find<Button>(R.id.cart_continue).apply {
            visibility = if (empty) View.GONE else View.VISIBLE
            setOnClickListener { host.showCheckout() }
        }
        find<Button>(R.id.cart_menu).setOnClickListener { host.showTab("Buscar") }
        find<Button>(R.id.cart_clear).apply {
            visibility = if (empty) View.GONE else View.VISIBLE
            setOnClickListener {
                host.confirm("Vaciar carrito", "¿Eliminar todas las comidas del carrito?") {
                    store.clearCart()
                    host.refreshScreen()
                }
            }
        }
    }
}
