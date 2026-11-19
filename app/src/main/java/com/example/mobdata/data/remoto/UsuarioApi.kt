package com.example.mobdata.data.remoto

import com.google.gson.annotations.SerializedName

// ==========================================
// DATA CLASS remota (DTO) para la funcionalidad "lista de usuarios".
//
// Este modelo representa lo que llega desde la API REST, NO la entidad Room
// local (com.example.mobdata.data.Usuario). Se mantiene separado a propósito:
//   - El Usuario local guarda el hash BCrypt de la contraseña (RF01).
//   - El UsuarioApi expone el perfil público que devuelve el servidor,
//     sin nunca transportar contraseñas ni hashes.
//
// @SerializedName vincula cada propiedad de Kotlin con la clave exacta del JSON,
// incluso si el nombre no coincide (aquí coinciden, pero se deja explícito para
// que el mapeo no se rompa si el backend cambia mayúsculas/minúsculas).
// ==========================================
data class UsuarioApi(
    @SerializedName("id") val id: Int,
    @SerializedName("alias") val alias: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("activo") val activo: Boolean,
    @SerializedName("fechaRegistro") val fechaRegistro: String
)

// ==========================================
// ENVOLVENTE de la respuesta.
// La API devuelve { "data": [...], "total": N } en vez de un array "plano".
// Modelar la envolvente permite acceder a metadatos (total, paginación futura)
// y deja el mapeo listo para añadir campos como "page" o "hasMore" sin tocar
// el ApiService ni el Repository.
// ==========================================
data class RespuestaUsuariosApi(
    @SerializedName("data") val data: List<UsuarioApi>,
    @SerializedName("total") val total: Int
)