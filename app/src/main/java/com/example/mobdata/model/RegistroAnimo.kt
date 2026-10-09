package com.example.mobdata.model

// ==========================================
// MODELO DE DATOS (refleja la entidad REGISTRO_ANIMO del modelo relacional)
// ==========================================
data class RegistroAnimo(
    val id: Long = System.currentTimeMillis(),
    val emocion: String,
    val intensidad: Int,
    val nota: String,
    val fechaHora: String
)

// Estado observable del formulario de check-in
data class EstadoFormularioAnimo(
    val emocionSeleccionada: String = "",
    val intensidad: Int = 0,
    val notaTexto: String = "",
    val mensajeError: String? = null,
    val guardadoExitoso: Boolean = false
)

// Estado observable del login
data class EstadoLogin(
    val alias: String = "",
    val mensajeError: String? = null
)
