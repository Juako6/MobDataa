package com.example.mobdata.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// ==========================================
// ENTIDAD ROOM: registro de estado de ánimo.
// `sincronizado` = false mientras el dato solo existe en el teléfono
// (RNF01 offline-first: queda pendiente de sincronizar con la API de prueba).
// ==========================================
@Entity(tableName = "registros_animo")
data class RegistroAnimo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val emocion: String,
    val intensidad: Int,
    val nota: String,
    val fecha: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "0") val sincronizado: Boolean = false
)
