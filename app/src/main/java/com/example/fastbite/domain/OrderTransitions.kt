package com.example.fastbite.domain

/** Estados finales inmutables: un pedido entregado o cancelado no vuelve a estar pendiente. */
object OrderTransitions {
    fun canChange(current: String, target: String): Boolean = when (target) {
        "DELIVERED" -> current == "PENDING" || current == "CONFIRMED"
        "CANCELLED" -> current == "PENDING"
        else -> false
    }
}
