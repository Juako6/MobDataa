package com.example.mobdata.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// ==========================================
// ENTIDAD ROOM (RF01): cuenta local de acceso.
// La contraseña nunca se guarda en texto plano: se almacena
// su hash BCrypt (con sal) calculado en el dispositivo.
// El alias es único: se indexa y no admite dos cuentas iguales.
// ==========================================
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["alias"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alias: String,
    val passwordHash: String,
    val fechaCreacion: Long = System.currentTimeMillis()
)
