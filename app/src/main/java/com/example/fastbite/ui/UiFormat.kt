package com.example.fastbite.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UiFormat {
    fun money(cents: Int): String = String.format(Locale.US, "$%.2f", cents / 100.0)
    fun date(timestamp: Long): String = SimpleDateFormat("dd/MM/yyyy · HH:mm", Locale("es", "SV")).format(Date(timestamp))
    fun status(value: String): String = when (value) {
        "CONFIRMED" -> "Confirmado"
        "DELIVERED" -> "Entregado"
        "CANCELLED" -> "Cancelado"
        else -> "Pendiente"
    }
}
