package com.example.fastbite.ui

import com.example.fastbite.R

/** Fotografías incluidas en la app: funcionan sin conexión y cada plato tiene su propio recurso. */
object FoodImages {
    fun forFood(id: String): Int = when (id) {
        "burger_classic" -> R.drawable.food_burger_classic
        "burger_double" -> R.drawable.food_burger_double
        "burger_chicken" -> R.drawable.food_burger_chicken
        "pizza_margherita" -> R.drawable.food_pizza_margherita
        "pizza_pepperoni" -> R.drawable.food_pizza_pepperoni
        "pizza_veggie" -> R.drawable.food_pizza_veggie
        "sushi_salmon" -> R.drawable.food_sushi_salmon
        "sushi_california" -> R.drawable.food_sushi_california
        "salad_caesar" -> R.drawable.food_salad_caesar
        "water" -> R.drawable.food_water
        "wings" -> R.drawable.food_wings
        "cheesecake" -> R.drawable.food_cheesecake
        "brownie" -> R.drawable.food_brownie
        "soda" -> R.drawable.food_soda
        "fries" -> R.drawable.food_fries
        "guacamole" -> R.drawable.food_guacamole
        "horchata" -> R.drawable.food_horchata
        "lemonade" -> R.drawable.food_lemonade
        "salad_garden" -> R.drawable.food_salad_garden
        "tacos" -> R.drawable.food_tacos
        "tequenos" -> R.drawable.food_tequenos
        "icecream" -> R.drawable.food_icecream
        else -> R.drawable.banner_hamburguesa
    }
}
