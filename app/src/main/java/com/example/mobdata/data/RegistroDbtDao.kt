package com.example.mobdata.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// DAO CRUD de registros_dbt (RF03, RF04)
@Dao
interface RegistroDbtDao {

    @Insert
    suspend fun insertar(registro: RegistroDbt): Long

    @Query("SELECT * FROM registros_dbt ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<RegistroDbt>>

    @Query("SELECT * FROM registros_dbt WHERE id = :id")
    suspend fun obtenerPorId(id: Long): RegistroDbt?

    @Update
    suspend fun actualizar(registro: RegistroDbt)

    @Delete
    suspend fun eliminar(registro: RegistroDbt)

    @Query("DELETE FROM registros_dbt WHERE id = :id")
    suspend fun eliminarPorId(id: Long)
}
