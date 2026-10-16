package com.example.mobdata.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// ==========================================
// ENTIDAD ROOM (RF03): diario de habilidades DBT.
// La habilidad se elige del catálogo; se guarda su nombre y categoría
// para que el registro siga siendo legible aunque el catálogo cambie.
// ==========================================
@Entity(tableName = "registros_dbt")
data class RegistroDbt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habilidad: String,
    val categoria: String,
    val situacion: String,
    val intensidad: Int,
    val utilidad: Int,
    val fecha: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "0") val sincronizado: Boolean = false
)
