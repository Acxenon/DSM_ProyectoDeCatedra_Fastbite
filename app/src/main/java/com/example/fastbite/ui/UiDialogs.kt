package com.example.fastbite.ui

import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.fastbite.MainActivity
import com.example.fastbite.R
import com.example.fastbite.domain.SalvadorPhone

/** Diálogos compartidos; el formulario de entrega utiliza dialog_address.xml. */
class UiDialogs(private val host: MainActivity) {
    fun info(title: String, message: String) {
        AlertDialog.Builder(host).setTitle(title).setMessage(message)
            .setPositiveButton("Entendido", null).show()
    }

    fun confirm(title: String, message: String, action: () -> Unit) {
        AlertDialog.Builder(host).setTitle(title).setMessage(message)
            .setNegativeButton("Volver", null)
            .setPositiveButton("Confirmar") { _, _ -> action() }.show()
    }

    fun address(saved: () -> Unit) {
        val view = host.layoutInflater.inflate(R.layout.dialog_address, null, false)
        val address = view.findViewById<EditText>(R.id.address_input)
        val phone = view.findViewById<EditText>(R.id.phone_input)
        address.setText(host.store.address)
        phone.setText(SalvadorPhone.forEditing(host.store.phone))
        val dialog = AlertDialog.Builder(host).setTitle("Datos de entrega").setView(view)
            .setNegativeButton("Volver", null).setPositiveButton("Guardar", null).create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val newAddress = address.text.toString().trim()
                val newPhone = phone.text.toString().trim()
                when {
                    newAddress.length < 8 -> address.error = "Escribe una dirección completa"
                    !SalvadorPhone.isValid(newPhone) -> phone.error = "El teléfono debe tener exactamente 8 dígitos"
                    else -> {
                        host.store.address = newAddress
                        host.store.phone = newPhone
                        host.state.draftAddress = newAddress
                        host.state.draftPhone = newPhone
                        dialog.dismiss()
                        saved()
                    }
                }
            }
        }
        dialog.show()
    }
}
