package com.example.mobdata.data

import com.example.mobdata.model.Credenciales
import com.example.mobdata.util.Seguridad

// ==========================================
// REPOSITORY de cuentas locales (RF01).
// Única puerta de acceso a la tabla `usuarios`:
// ViewModel -> UsuarioRepository -> UsuarioDao -> Room.
// El hachado y la verificación de contraseñas se delegan en Seguridad,
// la contraseña en claro nunca se guarda ni se devuelve.
// ==========================================
class UsuarioRepository(db: AppDatabase) {

    private val usuarioDao = db.usuarioDao()

    // ---------- CREATE: registro de una cuenta nueva ----------
    // Devuelve null si se registró correctamente, o el mensaje de error
    // si el alias ya estaba tomado (la inserción devuelve -1 por el índice único).
    suspend fun registrar(alias: String, password: String): String? {
        val aliasLimpio = alias.trim()
        val hash = Seguridad.hashPassword(password)
        val id = usuarioDao.insertar(
            Usuario(alias = aliasLimpio, passwordHash = hash)
        )
        return Credenciales.resultadoRegistro(id)
    }

    // ---------- READ: inicio de sesión ----------
    // Devuelve el Usuario si el alias existe y la contraseña coincide;
    // null si el alias no existe o la contraseña es incorrecta
    // (mismo mensaje en ambos casos para no revelar qué alias existen).
    suspend fun autenticar(alias: String, password: String): Usuario? {
        val usuario = usuarioDao.obtenerPorAlias(alias.trim()) ?: return null
        return if (Seguridad.verificarPassword(password, usuario.passwordHash)) usuario else null
    }

    // ---------- UPDATE: cambio de contraseña ----------
    suspend fun cambiarPassword(usuario: Usuario, passwordNueva: String) {
        usuarioDao.actualizar(usuario.copy(passwordHash = Seguridad.hashPassword(passwordNueva)))
    }

    // ---------- Consulta de disponibilidad ----------
    suspend fun aliasExiste(alias: String): Boolean =
        usuarioDao.obtenerPorAlias(alias.trim()) != null

    suspend fun contar(): Int = usuarioDao.contar()
}
