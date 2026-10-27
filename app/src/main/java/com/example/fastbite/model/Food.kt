package com.example.fastbite.model

data class Food(val id: String, val name: String, val description: String, val category: String,
                val kind: String, val price: Int, val emoji: String, val popular: Boolean = false)

