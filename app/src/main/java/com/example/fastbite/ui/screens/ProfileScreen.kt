package com.example.fastbite.ui.screens

import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.fastbite.MainActivity
import com.example.fastbite.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileScreen(host: MainActivity) : Screen(host, R.layout.screen_profile) {
    init {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            find<TextView>(R.id.profile_display_name).text = "👤 ${user.displayName.orEmpty().ifBlank { "Completa tu nombre" }}"
            find<TextView>(R.id.profile_email).text = user.email.orEmpty()
            val name: EditText = find(R.id.profile_name)
            name.setText(user.displayName.orEmpty())
            val save: Button = find(R.id.profile_save)
            save.setOnClickListener {
                val fullName = name.text.toString().trim()
                if (fullName.isBlank()) { name.error = "Ingresa tu nombre y apellido"; return@setOnClickListener }
                save.isEnabled = false
                name.isEnabled = false
                user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(fullName).build()).addOnCompleteListener { task ->
                    if (!host.isFinishing && !host.isDestroyed) {
                        save.isEnabled = true
                        name.isEnabled = true
                        if (task.isSuccessful) {
                            host.hideKeyboard()
                            host.toast("Nombre guardado")
                            if (state.tab == "Perfil" && state.mode.isEmpty()) host.refreshScreen()
                        } else host.toast("No se pudo guardar el nombre. Revisa tu conexión.")
                    }
                }
            }
        }
        bindOptions()
        find<Button>(R.id.profile_logout).setOnClickListener {
            host.confirm("Cerrar sesión", "Tu carrito e historial se conservarán para esta cuenta.") {
                FirebaseAuth.getInstance().signOut()
                host.goToLogin()
            }
        }
    }

    override fun onStoreChanged() { bindOptions() }

    private fun bindOptions() {
        val options: LinearLayout = find(R.id.profile_options)
        options.removeAllViews()
        components.profileOption(options, "📍 Dirección guardada", store.address.ifBlank { "Agrega dirección y teléfono" }) {
            host.dialogs.address { host.refreshScreen() }
        }
        components.profileOption(options, "💵 Método de pago", "Efectivo al recibir") {
            host.info("Pago en efectivo", "Los pedidos se confirman con pago en efectivo al recibir. No se guardan tarjetas ni se procesan cobros desde esta aplicación.")
        }
        components.profileOption(options, "▤ Historial de pedidos", "${store.orders.size} pedidos guardados") { host.showTab("Pedidos") }
        components.profileOption(options, "? Centro de ayuda", "Pedidos, sincronización y pago") {
            host.info("Ayuda de FastBite", "1. Añade comidas desde Inicio o Buscar.\n2. Ajusta cantidades en el carrito.\n3. Completa dirección y teléfono.\n4. Confirma y consulta Pedidos.\n\nPuedes marcar pedidos como entregados, cancelar pedidos pendientes y repetir compras. Los pedidos pendientes de sincronizar se conservan aquí; toca ↻ en Pedidos para reintentar.\n\nLa preparación y entrega requieren el sistema de gestión del restaurante. Esta versión no procesa pagos ni asigna repartidores.")
        }
    }
}
