package com.example.fastbite.ui.screens

import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.fastbite.MainActivity
import com.example.fastbite.R
import com.example.fastbite.data.FoodCatalog
import com.example.fastbite.ui.onTextChanged

class SearchScreen(host: MainActivity) : Screen(host, R.layout.screen_search) {
    private val results: LinearLayout = find(R.id.search_food_list)

    init {
        find<EditText>(R.id.search_input).apply {
            setText(state.searchQuery)
            onTextChanged { state.searchQuery = it; populate() }
        }
        components.chips(find(R.id.search_categories), listOf("Todos", "Entradas", "Platos fuertes", "Bebidas", "Postres"), state.searchCategory) {
            state.searchCategory = it
            host.hideKeyboard()
            host.refreshScreen()
        }
        populate()
    }

    private fun populate() {
        results.removeAllViews()
        val foods = FoodCatalog.search(state.searchQuery, state.searchCategory)
        find<TextView>(R.id.search_count).text = "${foods.size} opciones"
        if (foods.isEmpty()) components.empty(results, "No encontramos resultados", "Prueba otro nombre o selecciona Todos.")
        foods.forEach { components.food(results, it) {} }
    }
}
