package com.example.fastbite

import com.example.fastbite.data.FoodCatalog

import org.junit.Assert.*
import org.junit.Test

class FoodCatalogTest {
    @Test fun searchIgnoresCaseAndAccents() {
        assertTrue(FoodCatalog.search("CLASICA", "Todos").any { it.id == "burger_classic" })
        assertTrue(FoodCatalog.search("  LIMON  ", "Bebidas").any { it.id == "lemonade" })
    }
    @Test fun categoryAndQueryAreAppliedTogether() {
        assertTrue(FoodCatalog.search("pizza", "Bebidas").isEmpty())
        assertTrue(FoodCatalog.search("", "Pizza").all { it.kind == "Pizza" })
    }
    @Test fun canSearchIngredients() {
        assertTrue(FoodCatalog.search("pepino", "Todos").any { it.id == "sushi_california" })
    }
    @Test fun catalogHasUniqueIdsAndPositivePrices() {
        val foods = FoodCatalog.items
        assertEquals(foods.size, foods.map { it.id }.toSet().size)
        assertTrue(foods.all { it.price > 0 })
        assertNull(FoodCatalog.find("unknown"))
    }
}
