package com.example.mobdata.model

// ==========================================
// REGLAS DE VALIDACIÓN puras (sin dependencias de Android,
// por eso se pueden probar con tests unitarios JVM).
// Cada función devuelve el mensaje de error o null si todo es válido.
// Ningún registro incompleto o inválido llega a Room.
// ==========================================
object Validaciones {

    const val ALIAS_MIN = 3
    const val ALIAS_MAX = 20
    const val TEXTO_CORTO_MIN = 3
    const val TEXTO_CORTO_MAX = 150
    const val TEXTO_LARGO_MAX = 200

    // RNF03: patrones de datos personales que la app no debe almacenar
    private val REGEX_EMAIL = Regex("[\\w.+-]+@[\\w-]+\\.[\\w.-]+")
    private val REGEX_RUT = Regex("\\b\\d{1,2}\\.?\\d{3}\\.?\\d{3}-[\\dkK]\\b")
    private val REGEX_TELEFONO = Regex("\\+?\\d(?:[ -]?\\d){7,}")
    private val REGEX_ALIAS = Regex("^[\\p{L}0-9_.-]+$")

    private const val MSG_DATOS_PERSONALES =
        "No ingreses datos personales reales (RUT, correo, teléfono). Usa solo datos ficticios."

    fun contieneDatosPersonales(texto: String): Boolean =
        REGEX_EMAIL.containsMatchIn(texto) ||
            REGEX_RUT.containsMatchIn(texto) ||
            REGEX_TELEFONO.containsMatchIn(texto)

    // Texto obligatorio de largo acotado y sin datos personales
    private fun validarTextoObligatorio(valor: String, campo: String): String? {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() -> "Falta completar: ${campo.replaceFirstChar { it.lowercase() }}."
            limpio.length < TEXTO_CORTO_MIN ->
                "$campo debe tener al menos $TEXTO_CORTO_MIN caracteres."
            limpio.length > TEXTO_CORTO_MAX ->
                "$campo no puede superar los $TEXTO_CORTO_MAX caracteres."
            contieneDatosPersonales(limpio) -> MSG_DATOS_PERSONALES
            else -> null
        }
    }

    // Texto opcional: puede ir vacío, pero si se escribe debe ser válido
    private fun validarTextoOpcional(valor: String, campo: String): String? {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() -> null
            limpio.length > TEXTO_LARGO_MAX ->
                "$campo no puede superar los $TEXTO_LARGO_MAX caracteres."
            contieneDatosPersonales(limpio) -> MSG_DATOS_PERSONALES
            else -> null
        }
    }

    private fun escalaValida(valor: Int) = valor in 1..5

    // ---------- RF01: alias de ingreso ----------
    fun validarAlias(alias: String): String? {
        val limpio = alias.trim()
        return when {
            limpio.isEmpty() -> "El alias es obligatorio."
            limpio.length < ALIAS_MIN -> "El alias debe tener al menos $ALIAS_MIN caracteres."
            limpio.length > ALIAS_MAX -> "El alias no puede superar los $ALIAS_MAX caracteres."
            contieneDatosPersonales(limpio) -> MSG_DATOS_PERSONALES
            !REGEX_ALIAS.matches(limpio) ->
                "Usa solo letras, números, punto, guion o guion bajo (sin espacios)."
            else -> null
        }
    }

    // ---------- Estado de ánimo ----------
    fun validarCheckIn(emocion: String, intensidad: Int, nota: String = ""): String? = when {
        emocion.isEmpty() ->
            "Debes seleccionar una emoción."
        !escalaValida(intensidad) ->
            "Selecciona un nivel de intensidad (1 al 5)."
        else -> validarTextoOpcional(nota, "La nota")
    }

    // ---------- RF02: Neurodesarrollo ----------
    fun validarNeurodesarrollo(
        rutina: String,
        organizacionTareas: String,
        tipoEpisodio: String,
        estrategia: String,
        resultado: Int
    ): String? =
        validarTextoObligatorio(rutina, "La rutina")
            ?: validarTextoOpcional(organizacionTareas, "La organización de tareas")
            ?: (if (tipoEpisodio !in OPCIONES_EPISODIO) "Selecciona el tipo de episodio." else null)
            ?: validarTextoObligatorio(estrategia, "La estrategia utilizada")
            ?: (if (!escalaValida(resultado)) "Indica el resultado percibido (1 al 5)." else null)

    // ---------- RF03: Habilidades DBT ----------
    fun validarDbt(
        habilidad: String,
        situacion: String,
        intensidad: Int,
        utilidad: Int
    ): String? = when {
        habilidad.isBlank() -> "Selecciona la habilidad practicada del catálogo."
        else -> validarTextoObligatorio(situacion, "La emoción o situación")
            ?: (if (!escalaValida(intensidad)) "Indica el nivel de intensidad (1 al 5)." else null)
            ?: (if (!escalaValida(utilidad)) "Indica la utilidad percibida (1 al 5)." else null)
    }
}
