package com.example.mobdata.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

// ==========================================
// DAO de usuarios (RF01).
// Create -> insertar | Read -> obtenerPorAlias / contar
// Update -> actualizar (cambio de contraseña)
// ==========================================
@Dao
interface UsuarioDao {

    // IGNORE: si el alias ya existe devuelve -1 y no sobreescribe la cuenta
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE alias = :alias LIMIT 1")
    suspend fun obtenerPorAlias(alias: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Update
    suspend fun actualizar(usuario: Usuario)
}
