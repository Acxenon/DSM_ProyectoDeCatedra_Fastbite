package com.example.fastbite.model

import org.json.JSONObject

data class OrderLine(val foodId: String, val name: String, val emoji: String, val price: Int, val quantity: Int) {
    fun json() = JSONObject().put("foodId", foodId).put("name", name).put("emoji", emoji)
        .put("price", price).put("quantity", quantity)
    companion object {
        fun from(j: JSONObject) = OrderLine(j.getString("foodId"), j.getString("name"), j.optString("emoji", "🍽"), j.getInt("price"), j.getInt("quantity"))
    }
}

