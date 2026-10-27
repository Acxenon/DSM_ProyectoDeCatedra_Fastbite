package com.example.fastbite.ui.screens

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.fastbite.MainActivity
import com.example.fastbite.R
import com.example.fastbite.data.FoodCatalog
import com.google.firebase.auth.FirebaseAuth

class HomeScreen(host: MainActivity) : Screen(host, R.layout.screen_home) {
    private val foods: LinearLayout = find(R.id.home_food_list)

    init {
        find<TextView>(R.id.home_address).text = store.address.ifBlank { "Agrega tu dirección de entrega ›" }
        find<View>(R.id.home_delivery).setOnClickListener { host.dialogs.address { host.refreshScreen() } }
        find<Button>(R.id.home_search).setOnClickListener { host.showTab("Buscar") }
        val name = FirebaseAuth.getInstance().currentUser?.displayName?.substringBefore(' ').orEmpty().ifBlank { "bienvenido" }
        find<TextView>(R.id.home_greeting).text = "Hola, $name 👋"
        components.chips(find(R.id.home_categories), listOf("Todos", "Hamburguesas", "Pizza", "Sushi", "Ensaladas", "Tacos", "Entradas", "Bebidas", "Postres"), state.homeCategory) {
            state.homeCategory = it
            host.refreshScreen()
        }
        find<Button>(R.id.home_favorites).apply {
            text = if (state.onlyFavorites) "Ver todos" else "♥ Favoritos"
            setOnClickListener { state.onlyFavorites = !state.onlyFavorites; host.refreshScreen() }
        }
        find<TextView>(R.id.home_list_title).text = if (state.onlyFavorites) "Tus favoritos" else "Populares ahora"
        populate()
    }

    private fun populate() {
        foods.removeAllViews()
        val results = FoodCatalog.search("", state.homeCategory).filter { !state.onlyFavorites || it.id in store.favorites }
        if (results.isEmpty()) components.empty(foods, "No hay comidas aquí todavía", "Marca comidas con el corazón para guardarlas como favoritas.")
        results.sortedByDescending { it.popular }.forEach { food ->
            components.food(foods, food) { if (state.onlyFavorites) populate() }
        }
    }

}
