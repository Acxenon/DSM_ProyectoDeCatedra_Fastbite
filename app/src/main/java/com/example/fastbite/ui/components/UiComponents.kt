package com.example.fastbite.ui.components

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.fastbite.MainActivity
import com.example.fastbite.R
import com.example.fastbite.model.Food
import com.example.fastbite.model.OrderLine
import com.example.fastbite.domain.OrderPricing
import com.example.fastbite.ui.UiFormat
import com.example.fastbite.ui.FoodImages

/** Enlace de datos y eventos de las tarjetas. Sus tamaños, colores y distribución están en XML. */
class UiComponents(private val host: MainActivity) {
    private fun inflate(layout: Int, parent: LinearLayout): View = host.layoutInflater.inflate(layout, parent, false)

    fun empty(parent: LinearLayout, title: String, detail: String, action: (() -> Unit)? = null) {
        val view = inflate(R.layout.view_empty, parent)
        view.findViewById<TextView>(R.id.empty_title).text = title
        view.findViewById<TextView>(R.id.empty_description).text = detail
        view.findViewById<Button>(R.id.empty_action).apply {
            visibility = if (action == null) View.GONE else View.VISIBLE
            setOnClickListener { action?.invoke() }
        }
        parent.addView(view)
    }

    fun chips(parent: LinearLayout, values: List<String>, selected: String, onSelect: (String) -> Unit) {
        parent.removeAllViews()
        values.forEach { value ->
            val chip = inflate(R.layout.item_category_chip, parent) as TextView
            chip.text = value
            chip.isSelected = value == selected
            chip.setBackgroundResource(if (chip.isSelected) R.drawable.bg_fb_chip_active else R.drawable.bg_fb_chip)
            chip.setTextColor(host.getColor(if (chip.isSelected) R.color.fb_white else R.color.fb_muted))
            chip.setOnClickListener { onSelect(value) }
            parent.addView(chip)
        }
    }

    fun food(parent: LinearLayout, food: Food, favoriteChanged: () -> Unit) {
        val view = inflate(R.layout.item_food, parent)
        view.findViewById<ImageView>(R.id.food_photo).apply {
            visibility = View.VISIBLE
            setImageResource(FoodImages.forFood(food.id))
            contentDescription = food.name
        }
        view.findViewById<TextView>(R.id.food_name).text = food.name
        view.findViewById<TextView>(R.id.food_description).text = food.description
        view.findViewById<TextView>(R.id.food_price).text = UiFormat.money(food.price)
        view.findViewById<Button>(R.id.food_favorite).apply {
            text = if (food.id in host.store.favorites) "♥" else "♡"
            contentDescription = "Favorito: ${food.name}"
            setOnClickListener {
                host.store.toggleFavorite(food.id)
                text = if (food.id in host.store.favorites) "♥" else "♡"
                favoriteChanged()
            }
        }
        view.findViewById<Button>(R.id.food_add).apply {
            contentDescription = "Agregar ${food.name} al carrito"
            setOnClickListener {
                if (host.store.add(food.id)) {
                    host.updateCartBadge()
                    host.toast("${food.name} agregado al carrito")
                } else host.toast("Máximo 20 unidades de cada comida")
            }
        }
        parent.addView(view)
    }

    fun cartLines(parent: LinearLayout, changed: () -> Unit) {
        parent.removeAllViews()
        host.store.lines().forEach { line -> cartLine(parent, line, changed) }
    }

    private fun cartLine(parent: LinearLayout, line: OrderLine, changed: () -> Unit) {
        val view = inflate(R.layout.item_cart_line, parent)
        view.findViewById<ImageView>(R.id.line_photo).apply {
            setImageResource(FoodImages.forFood(line.foodId))
            contentDescription = line.name
        }
        view.findViewById<TextView>(R.id.line_name).text = line.name
        view.findViewById<TextView>(R.id.line_unit_price).text = "${UiFormat.money(line.price)} por unidad"
        view.findViewById<TextView>(R.id.line_total).text = UiFormat.money(line.price * line.quantity)
        view.findViewById<TextView>(R.id.line_quantity).text = line.quantity.toString()
        view.findViewById<Button>(R.id.line_decrease).setOnClickListener {
            if (line.quantity == 1) {
                host.confirm("Eliminar comida", "¿Quitar ${line.name} del carrito?") {
                    host.store.quantity(line.foodId, 0)
                    host.updateCartBadge()
                    changed()
                }
            } else {
                host.store.quantity(line.foodId, line.quantity - 1)
                host.updateCartBadge()
                changed()
            }
        }
        view.findViewById<Button>(R.id.line_increase).setOnClickListener {
            if (host.store.add(line.foodId)) {
                host.updateCartBadge()
                changed()
            } else host.toast("Máximo 20 unidades de cada comida")
        }
        parent.addView(view)
    }

    fun summary(view: View, coupon: Boolean) {
        val subtotal = host.store.subtotal()
        view.findViewById<TextView>(R.id.summary_subtotal).text = UiFormat.money(subtotal)
        view.findViewById<View>(R.id.summary_discount_row).visibility = if (coupon) View.VISIBLE else View.GONE
        view.findViewById<TextView>(R.id.summary_discount).text = UiFormat.money(-OrderPricing.discount(subtotal, coupon))
        view.findViewById<TextView>(R.id.summary_delivery).text = UiFormat.money(OrderPricing.delivery(subtotal))
        view.findViewById<TextView>(R.id.summary_total).text = UiFormat.money(OrderPricing.total(subtotal, coupon))
    }

    fun profileOption(parent: LinearLayout, title: String, detail: String, action: () -> Unit) {
        val view = inflate(R.layout.item_profile_option, parent)
        view.findViewById<TextView>(R.id.option_title).text = title
        view.findViewById<TextView>(R.id.option_description).text = detail
        view.setOnClickListener { action() }
        parent.addView(view)
    }
}
