package com.example.mobdata.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// ==========================================
// ENTIDAD ROOM: refleja la tabla REGISTRO_ANIMO del modelo relacional.
// Se guarda en la base de datos local del dispositivo.
// ==========================================
@Entity(tableName = "registros_animo")
data class RegistroAnimo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val emocion: String,
    val intensidad: Int,
    val nota: String,
    val fecha: Long = System.currentTimeMillis()
)
