package com.example.fastbite.data

import com.example.fastbite.model.Food
import java.text.Normalizer

object FoodCatalog {
    val items = listOf(
        Food("burger_classic", "Hamburguesa clásica", "Carne, queso cheddar, lechuga y salsa de la casa.", "Platos fuertes", "Hamburguesas", 650, "🍔", true),
        Food("burger_double", "Doble cheeseburger", "Doble carne y doble queso, con pepinillos.", "Platos fuertes", "Hamburguesas", 850, "🍔", true),
        Food("burger_chicken", "Hamburguesa de pollo", "Pollo crujiente, ensalada fresca y mayonesa.", "Platos fuertes", "Hamburguesas", 600, "🍔"),
        Food("pizza_margherita", "Pizza Margarita", "Pizza personal con tomate, mozzarella y albahaca.", "Platos fuertes", "Pizza", 750, "🍕", true),
        Food("pizza_pepperoni", "Pizza Pepperoni", "Pizza personal con mozzarella y pepperoni.", "Platos fuertes", "Pizza", 850, "🍕", true),
        Food("pizza_veggie", "Pizza vegetariana", "Pizza personal con hongos, pimientos y aceitunas.", "Platos fuertes", "Pizza", 800, "🍕"),
        Food("sushi_salmon", "Roll de salmón", "8 piezas con salmón, aguacate y queso crema.", "Platos fuertes", "Sushi", 950, "🍣", true),
        Food("sushi_california", "California roll", "8 piezas con surimi, pepino y aguacate.", "Platos fuertes", "Sushi", 800, "🍣"),
        Food("salad_caesar", "Ensalada César", "Pollo, lechuga, crutones, parmesano y aderezo.", "Platos fuertes", "Ensaladas", 550, "🥗", true),
        Food("salad_garden", "Ensalada de la huerta", "Lechuga, tomate, pepino y vinagreta de limón.", "Platos fuertes", "Ensaladas", 450, "🥗"),
        Food("tacos", "Tacos de pollo", "3 tacos con pollo, cebolla, cilantro y salsa.", "Platos fuertes", "Tacos", 600, "🌮"),
        Food("tequenos", "Tequeños de queso", "6 palitos de queso crujientes con salsa.", "Entradas", "Entradas", 450, "🧀"),
        Food("guacamole", "Guacamole fresco", "Aguacate, tomate y limón; incluye totopos.", "Entradas", "Entradas", 400, "🥑"),
        Food("fries", "Papas fritas", "Porción de papas doradas con salsa de tomate.", "Entradas", "Entradas", 250, "🍟"),
        Food("wings", "Alitas BBQ", "6 alitas con salsa BBQ y aderezo ranch.", "Entradas", "Entradas", 650, "🍗"),
        Food("lemonade", "Limonada con menta", "Limonada natural fría, vaso de 16 oz.", "Bebidas", "Bebidas", 200, "🍋"),
        Food("horchata", "Horchata", "Bebida tradicional fría, vaso de 16 oz.", "Bebidas", "Bebidas", 200, "🥤"),
        Food("soda", "Refresco", "Refresco de cola, lata de 355 ml.", "Bebidas", "Bebidas", 150, "🥤"),
        Food("water", "Agua", "Botella de agua de 600 ml.", "Bebidas", "Bebidas", 100, "💧"),
        Food("brownie", "Brownie de chocolate", "Brownie con chocolate y nueces.", "Postres", "Postres", 300, "🍫"),
        Food("cheesecake", "Cheesecake", "Porción de cheesecake con salsa de fresa.", "Postres", "Postres", 350, "🍰"),
        Food("icecream", "Helado de vainilla", "Dos bolas de helado de vainilla.", "Postres", "Postres", 250, "🍨")
    )
    fun find(id: String) = items.firstOrNull { it.id == id }
    fun normalized(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "").lowercase(java.util.Locale.ROOT)
    fun search(query: String, category: String): List<Food> {
        val q = normalized(query.trim())
        return items.filter { (category == "Todos" || it.category == category || it.kind == category) &&
            normalized("${it.name} ${it.description} ${it.category} ${it.kind}").contains(q) }
    }
}
