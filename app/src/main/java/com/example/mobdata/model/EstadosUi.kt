package com.example.mobdata.model

// ==========================================
// ESTADOS OBSERVABLES de la UI (los consumen los ViewModel)
// idEdicion = 0 -> registro nuevo | idEdicion > 0 -> editando ese registro
// ==========================================

// ---- Estado de ánimo ----
data class EstadoFormularioAnimo(
    val emocionSeleccionada: String = "",
    val intensidad: Int = 0,
    val notaTexto: String = "",
    val mensajeError: String? = null,
    val guardadoExitoso: Boolean = false,
    val idEdicion: Long = 0,
    val fechaOriginal: Long = 0
) {
    val modoEdicion: Boolean get() = idEdicion != 0L
}

// ---- Neurodesarrollo (RF02) ----
data class EstadoFormularioNeuro(
    val rutina: String = "",
    val organizacionTareas: String = "",
    val tipoEpisodio: String = "",
    val estrategia: String = "",
    val resultado: Int = 0,
    val mensajeError: String? = null,
    val guardadoExitoso: Boolean = false,
    val idEdicion: Long = 0,
    val fechaOriginal: Long = 0
) {
    val modoEdicion: Boolean get() = idEdicion != 0L
}

// ---- Habilidades DBT (RF03) ----
data class EstadoFormularioDbt(
    val habilidad: String = "",
    val categoria: String = "",
    val situacion: String = "",
    val intensidad: Int = 0,
    val utilidad: Int = 0,
    val mensajeError: String? = null,
    val guardadoExitoso: Boolean = false,
    val idEdicion: Long = 0,
    val fechaOriginal: Long = 0
) {
    val modoEdicion: Boolean get() = idEdicion != 0L
}

// ---- Login ----
data class EstadoLogin(
    val alias: String = "",
    val mensajeError: String? = null
)

// Opciones de episodio del módulo Neurodesarrollo
val OPCIONES_EPISODIO = listOf("Sobrecarga", "Autorregulación", "Sin episodio")

// Formatea el timestamp guardado en la base de datos para mostrarlo en pantalla
fun formatearFecha(fecha: Long): String {
    val formato = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    return formato.format(java.util.Date(fecha))
}
