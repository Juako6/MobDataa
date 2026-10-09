package com.example.mobdata.model

// ==========================================
// ESTADOS OBSERVABLES de la UI (los consume el ViewModel)
// ==========================================

// Estado del formulario de check-in
data class EstadoFormularioAnimo(
    val emocionSeleccionada: String = "",
    val intensidad: Int = 0,
    val notaTexto: String = "",
    val mensajeError: String? = null,
    val guardadoExitoso: Boolean = false
)

// Estado del login
data class EstadoLogin(
    val alias: String = "",
    val mensajeError: String? = null
)

// Formatea el timestamp guardado en la base de datos para mostrarlo en pantalla
fun formatearFecha(fecha: Long): String {
    val formato = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    return formato.format(java.util.Date(fecha))
}
