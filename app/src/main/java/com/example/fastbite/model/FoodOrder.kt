package com.example.fastbite.model

import org.json.JSONObject
import org.json.JSONArray

data class FoodOrder(val id: String, val ownerId: String, val customer: String,
    val lines: List<OrderLine>, val address: String, val phone: String, val notes: String,
    val subtotal: Int, val discount: Int, val delivery: Int, val total: Int,
    val createdAt: Long, var updatedAt: Long, var status: String = "PENDING", var synced: Boolean = false) {
    fun json(): JSONObject = JSONObject().put("id", id).put("ownerId", ownerId).put("customer", customer)
        .put("lines", JSONArray().apply { lines.forEach { put(it.json()) } })
        .put("address", address).put("phone", phone).put("notes", notes).put("payment", "CASH")
        .put("subtotal", subtotal).put("discount", discount).put("delivery", delivery).put("total", total)
        .put("createdAt", createdAt).put("updatedAt", updatedAt).put("status", status).put("synced", synced)
    fun cloud(): Map<String, Any> = mapOf(
        "id" to id, "ownerId" to ownerId, "customer" to customer,
        "lines" to lines.map { mapOf("foodId" to it.foodId, "name" to it.name, "emoji" to it.emoji, "price" to it.price, "quantity" to it.quantity) },
        "address" to address, "phone" to phone, "notes" to notes, "payment" to "CASH",
        "subtotal" to subtotal, "discount" to discount, "delivery" to delivery, "total" to total,
        "createdAt" to createdAt, "updatedAt" to updatedAt, "status" to status)
    companion object {
        fun from(j: JSONObject): FoodOrder {
            val list = j.getJSONArray("lines")
            return FoodOrder(j.getString("id"), j.getString("ownerId"), j.optString("customer"),
                (0 until list.length()).map { OrderLine.from(list.getJSONObject(it)) },
                j.getString("address"), j.getString("phone"), j.optString("notes"),
                j.getInt("subtotal"), j.getInt("discount"), j.getInt("delivery"), j.getInt("total"),
                j.getLong("createdAt"), j.getLong("updatedAt"), j.getString("status"), j.optBoolean("synced"))
        }
    }
}

