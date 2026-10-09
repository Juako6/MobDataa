package com.example.mobdata.model

// ==========================================
// REGLAS DE VALIDACIÓN puras (sin dependencias de Android,
// por eso se pueden probar con tests unitarios JVM)
// ==========================================
object Validaciones {

    // RF02: check-in de estado de ánimo
    fun validarCheckIn(emocion: String, intensidad: Int): String? = when {
        emocion.isEmpty() ->
            "Debes seleccionar una emoción."
        intensidad < 1 || intensidad > 5 ->
            "Selecciona un nivel de intensidad (1 al 5)."
        else -> null
    }

    // RF01: alias de ingreso
    fun validarAlias(alias: String): String? {
        val limpio = alias.trim()
        return when {
            limpio.isEmpty() -> "El alias es obligatorio."
            limpio.length < 3 -> "El alias debe tener al menos 3 caracteres."
            else -> null
        }
    }
}
