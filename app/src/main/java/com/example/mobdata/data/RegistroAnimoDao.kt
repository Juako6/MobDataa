package com.example.mobdata.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ==========================================
// DAO CRUD de registros_animo.
// Create -> insertar | Read -> obtenerTodos / obtenerPorId
// Update -> actualizar | Delete -> eliminar / eliminarPorId
// obtenerTodos devuelve un Flow: la UI se actualiza sola cuando la tabla cambia.
// ==========================================
@Dao
interface RegistroAnimoDao {

    @Insert
    suspend fun insertar(registro: RegistroAnimo): Long

    @Query("SELECT * FROM registros_animo ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<RegistroAnimo>>

    @Query("SELECT * FROM registros_animo WHERE id = :id")
    suspend fun obtenerPorId(id: Long): RegistroAnimo?

    @Update
    suspend fun actualizar(registro: RegistroAnimo)

    @Delete
    suspend fun eliminar(registro: RegistroAnimo)

    @Query("DELETE FROM registros_animo WHERE id = :id")
    suspend fun eliminarPorId(id: Long)
}
