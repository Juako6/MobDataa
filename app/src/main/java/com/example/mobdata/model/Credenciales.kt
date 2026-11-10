package com.example.mobdata.model

// ==========================================
// REGLAS PURAS de credenciales y sesión (RF01).
// Sin dependencias de Android para poder probarse en JVM,
// igual que Validaciones. Las usa UsuarioRepository y SesionManager.
// ==========================================
object Credenciales {

    const val MSG_ALIAS_OCUPADO =
        "Ese alias ya está registrado. Elige otro o inicia sesión."

    // Mensaje único para fallo de autenticación: no revela si el alias
    // existe o si solo estaba mal la contraseña.
    const val MSG_CREDENCIALES_INVALIDAS = "Alias o contraseña incorrectos."

    // Traduce el resultado de la inserción en Room (-1 = alias duplicado)
    // al mensaje de error, o null si el registro fue exitoso.
    fun resultadoRegistro(idInsertado: Long): String? =
        if (idInsertado == -1L) MSG_ALIAS_OCUPADO else null

    // ¿La sesión guardada sigue vigente en el instante `ahora`?
    // Sin marca de expiración (0) se considera vencida.
    fun sesionVigente(expira: Long, ahora: Long): Boolean =
        expira != 0L && ahora <= expira
}
