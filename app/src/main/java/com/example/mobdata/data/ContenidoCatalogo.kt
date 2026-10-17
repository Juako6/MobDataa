package com.example.mobdata.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// ==========================================
// ENTIDAD ROOM (RF10): catálogo de contenidos administrados centralmente
// (guías de habilidades DBT y textos psicoeducativos).
// ==========================================
object TiposContenido {
    const val HABILIDAD_DBT = "HABILIDAD_DBT"
    const val PSICOEDUCATIVO = "PSICOEDUCATIVO"
}

@Entity(tableName = "catalogo_contenidos")
data class ContenidoCatalogo(
    @PrimaryKey val id: Long,
    val tipo: String,
    val categoria: String,
    val titulo: String,
    val descripcion: String
)
