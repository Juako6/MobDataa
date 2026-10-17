package com.example.mobdata.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// DAO CRUD de registros_neurodesarrollo (RF02, RF04)
@Dao
interface RegistroNeurodesarrolloDao {

    @Insert
    suspend fun insertar(registro: RegistroNeurodesarrollo): Long

    @Query("SELECT * FROM registros_neurodesarrollo ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<RegistroNeurodesarrollo>>

    @Query("SELECT * FROM registros_neurodesarrollo WHERE id = :id")
    suspend fun obtenerPorId(id: Long): RegistroNeurodesarrollo?

    @Update
    suspend fun actualizar(registro: RegistroNeurodesarrollo)

    @Delete
    suspend fun eliminar(registro: RegistroNeurodesarrollo)

    @Query("DELETE FROM registros_neurodesarrollo WHERE id = :id")
    suspend fun eliminarPorId(id: Long)
}
