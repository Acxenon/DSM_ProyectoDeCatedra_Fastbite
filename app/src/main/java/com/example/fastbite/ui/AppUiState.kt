package com.example.fastbite.ui

import android.os.Bundle

/** Estado de navegación y borradores, independiente de las vistas de cada pantalla. */
class AppUiState(saved: Bundle?) {
    var tab = saved?.getString("tab") ?: "Inicio"
    var mode = saved?.getString("mode").orEmpty()
    var homeCategory = saved?.getString("homeCategory") ?: "Todos"
    var searchCategory = saved?.getString("category") ?: "Todos"
    var searchQuery = saved?.getString("query").orEmpty()
    var orderQuery = saved?.getString("orderQuery").orEmpty()
    var orderFilter = saved?.getString("orderFilter") ?: "Todos"
    var onlyFavorites = saved?.getBoolean("onlyFavorites") ?: false
    var draftAddress: String? = saved?.getString("draftAddress")
    var draftPhone: String? = saved?.getString("draftPhone")
    var draftNotes = saved?.getString("draftNotes").orEmpty()

    fun save(out: Bundle) {
        out.putString("tab", tab)
        out.putString("mode", mode)
        out.putString("homeCategory", homeCategory)
        out.putString("query", searchQuery)
        out.putString("category", searchCategory)
        out.putString("orderQuery", orderQuery)
        out.putString("orderFilter", orderFilter)
        out.putBoolean("onlyFavorites", onlyFavorites)
        out.putString("draftAddress", draftAddress)
        out.putString("draftPhone", draftPhone)
        out.putString("draftNotes", draftNotes)
    }
}
