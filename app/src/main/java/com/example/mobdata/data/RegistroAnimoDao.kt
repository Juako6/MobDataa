package com.example.mobdata.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// ==========================================
// DAO: operaciones sobre la tabla registros_animo.
// obtenerTodos devuelve un Flow: la UI se actualiza
// automáticamente cada vez que la tabla cambia.
// ==========================================
@Dao
interface RegistroAnimoDao {

    @Query("SELECT * FROM registros_animo ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<RegistroAnimo>>

    @Insert
    suspend fun insertar(registro: RegistroAnimo): Long
}
