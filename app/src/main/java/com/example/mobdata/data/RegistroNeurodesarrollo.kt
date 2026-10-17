package com.example.mobdata.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// ==========================================
// ENTIDAD ROOM (RF02): registro del módulo Neurodesarrollo.
// Rutina, organización de tareas, episodio de sobrecarga/autorregulación,
// estrategia utilizada y resultado percibido (1 a 5).
// ==========================================
@Entity(tableName = "registros_neurodesarrollo")
data class RegistroNeurodesarrollo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rutina: String,
    val organizacionTareas: String,
    val tipoEpisodio: String,
    val estrategia: String,
    val resultado: Int,
    val fecha: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "0") val sincronizado: Boolean = false
)
