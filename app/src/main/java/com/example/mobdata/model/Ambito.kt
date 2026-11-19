package com.example.mobdata.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Mood
import androidx.compose.ui.graphics.vector.ImageVector

// ==========================================
// ÁMBITOS (módulos) de autorregistro de la app.
// El icono es un vector de Material Icons (antes era un emoji).
// ==========================================
enum class Ambito(
    val etiqueta: String,
    val icono: ImageVector,
    val descripcion: String
) {
    ANIMO("Estado de ánimo", Icons.Filled.Mood, "Emoción e intensidad del momento"),
    NEURODESARROLLO("Neurodesarrollo", Icons.Filled.Extension, "Rutinas, tareas y episodios de sobrecarga"),
    DBT("Habilidades DBT", Icons.Filled.Handyman, "Diario de habilidades practicadas");

    companion object {
        fun desdeNombre(nombre: String?): Ambito? = entries.find { it.name == nombre }
    }
}
