package com.example.fastbite.domain

/** Ocho dígitos nacionales; el prefijo +503 se presenta fuera del campo editable. */
object SalvadorPhone {
    fun isValid(value: String): Boolean = value.matches(Regex("[0-9]{8}"))

    /** Compatibilidad con teléfonos guardados anteriormente, sin truncar números incorrectos. */
    fun forEditing(saved: String): String {
        val compact = saved.trim().replace(" ", "").replace("-", "")
        val local = when {
            compact.startsWith("+503") && compact.length == 12 -> compact.removePrefix("+503")
            compact.startsWith("503") && compact.length == 11 -> compact.removePrefix("503")
            else -> compact
        }
        return if (isValid(local)) local else ""
    }

    fun display(saved: String): String = if (isValid(saved)) "+503 $saved" else saved
}
