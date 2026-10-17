package com.example.mobdata.model

// ==========================================
// ÁMBITOS (módulos) de autorregistro de la app.
// ==========================================
enum class Ambito(val etiqueta: String, val icono: String, val descripcion: String) {
    ANIMO("Estado de ánimo", "😊", "Emoción e intensidad del momento"),
    NEURODESARROLLO("Neurodesarrollo", "🧩", "Rutinas, tareas y episodios de sobrecarga"),
    DBT("Habilidades DBT", "🛠️", "Diario de habilidades practicadas");

    companion object {
        fun desdeNombre(nombre: String?): Ambito? = entries.find { it.name == nombre }
    }
}
