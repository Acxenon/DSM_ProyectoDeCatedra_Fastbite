package com.example.fastbite

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fastbite.data.FastBiteStore
import com.example.fastbite.ui.AppUiState
import com.example.fastbite.ui.UiDialogs
import com.example.fastbite.ui.components.UiComponents
import com.example.fastbite.ui.screens.Screen
import com.example.fastbite.ui.screens.HomeScreen
import com.example.fastbite.ui.screens.SearchScreen
import com.example.fastbite.ui.screens.OrdersScreen
import com.example.fastbite.ui.screens.ProfileScreen
import com.example.fastbite.ui.screens.CartScreen
import com.example.fastbite.ui.screens.CheckoutScreen
import com.google.firebase.auth.FirebaseAuth

/** Contenedor: autenticación, navegación y ciclo de vida. Cada pantalla tiene su XML y controlador. */
class MainActivity : AppCompatActivity() {
    lateinit var store: FastBiteStore
        private set
    lateinit var state: AppUiState
        private set
    lateinit var components: UiComponents
        private set
    lateinit var dialogs: UiDialogs
        private set
    lateinit var container: FrameLayout
        private set
    private var screen: Screen? = null
    private lateinit var cartBadge: TextView
    private val navigation = listOf(
        R.id.nav_home to "Inicio",
        R.id.nav_search to "Buscar",
        R.id.nav_orders to "Pedidos",
        R.id.nav_profile to "Perfil"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) { goToLogin(); return }
        store = FastBiteStore(this, user.uid)
        state = AppUiState(savedInstanceState)
        components = UiComponents(this)
        dialogs = UiDialogs(this)
        setContentView(R.layout.activity_main)
        container = findViewById(R.id.screen_container)
        cartBadge = findViewById(R.id.header_cart)
        val root = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val keyboard = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, maxOf(bars.bottom, keyboard.bottom))
            insets
        }
        findViewById<View>(R.id.header_back).setOnClickListener {
            if (state.mode == "checkout") showCart() else showTab("Inicio")
        }
        cartBadge.setOnClickListener { showCart() }
        navigation.forEach { (id, tab) -> findViewById<View>(id).setOnClickListener { showTab(tab) } }
        store.onChanged = { if (!isFinishing && !isDestroyed) screen?.onStoreChanged() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    state.mode == "checkout" -> showCart()
                    state.mode.isNotEmpty() -> showTab(state.tab)
                    state.tab != "Inicio" -> showTab("Inicio")
                    else -> { isEnabled = false; onBackPressedDispatcher.onBackPressed() }
                }
            }
        })
        present()
        store.refresh()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        screen?.captureState()
        if (::state.isInitialized) state.save(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        if (::store.isInitialized) store.onChanged = null
        super.onDestroy()
    }

    fun showTab(tab: String) {
        screen?.captureState()
        state.tab = tab
        state.mode = ""
        hideKeyboard()
        present()
        if (tab == "Pedidos") store.refresh()
    }

    fun showCart() {
        screen?.captureState()
        state.mode = "cart"
        hideKeyboard()
        present()
    }

    fun showCheckout() {
        screen?.captureState()
        state.mode = if (store.cart.isEmpty()) "cart" else "checkout"
        present()
    }

    fun refreshScreen() {
        screen?.captureState()
        present()
    }

    private fun present() {
        if (state.mode == "checkout" && store.cart.isEmpty()) state.mode = "cart"
        screen = when (state.mode) {
            "cart" -> CartScreen(this)
            "checkout" -> CheckoutScreen(this)
            else -> when (state.tab) {
                "Buscar" -> SearchScreen(this)
                "Pedidos" -> OrdersScreen(this)
                "Perfil" -> ProfileScreen(this)
                else -> HomeScreen(this)
            }
        }
        container.removeAllViews()
        container.addView(screen!!.view)
        navigation.forEach { (id, tab) ->
            val selected = state.tab == tab && state.mode.isEmpty()
            findViewById<TextView>(id).apply {
                isSelected = selected
                setTextColor(getColor(if (selected) R.color.fb_orange else R.color.fb_muted))
                if (selected) setBackgroundResource(R.drawable.bg_fb_pale) else setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        }
        updateCartBadge()
    }

    fun updateCartBadge() {
        val count = store.cart.values.sum()
        cartBadge.text = "🛒 $count"
        cartBadge.contentDescription = "Carrito con $count productos"
    }

    fun hideKeyboard() {
        currentFocus?.let {
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()
        }
    }

    fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    fun info(title: String, message: String) = dialogs.info(title, message)
    fun confirm(title: String, message: String, action: () -> Unit) = dialogs.confirm(title, message, action)
    fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
