package com.example.mobdata.model

import com.example.mobdata.data.remoto.UsuarioApi

object UsuariosFiltro {

    // Devuelve solo los usuarios con activo = true.
    // Lista vacía o null -> lista vacía (no lanza excepción).
    fun soloActivos(usuarios: List<UsuarioApi>?): List<UsuarioApi> =
        (usuarios ?: emptyList()).filter { it.activo }

    // Búsqueda tolerante a mayúsculas y espacios por alias o nombre.
    fun buscar(usuarios: List<UsuarioApi>?, texto: String): List<UsuarioApi> {
        val query = texto.trim().lowercase()
        if (query.isEmpty() || usuarios == null) return emptyList()
        return usuarios.filter {
            it.alias.lowercase().contains(query) ||
                    it.nombre.lowercase().contains(query)
        }
    }

    // Cuenta activos/inactivos en una sola pasada.
    fun resumenActivos(usuarios: List<UsuarioApi>?): ResumenUsuarios {
        val lista = usuarios ?: emptyList()
        val activos = lista.count { it.activo }
        return ResumenUsuarios(
            total = lista.size,
            activos = activos,
            inactivos = lista.size - activos
        )
    }
}

data class ResumenUsuarios(val total: Int, val activos: Int, val inactivos: Int)

