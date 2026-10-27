package com.example.fastbite.ui.screens

import android.view.View
import com.example.fastbite.MainActivity

/** Cada controlador enlaza un layout XML. MainActivity únicamente selecciona la pantalla. */
abstract class Screen(protected val host: MainActivity, layout: Int) {
    val view: View = host.layoutInflater.inflate(layout, host.container, false)
    protected val store get() = host.store
    protected val state get() = host.state
    protected val components get() = host.components
    protected fun <T : View> find(id: Int): T = view.findViewById(id)
    open fun captureState() {}
    open fun onStoreChanged() {}
}
