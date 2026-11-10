package com.example.mobdata.util

import at.favre.lib.crypto.bcrypt.BCrypt

// ==========================================
// SEGURIDAD (RF01): hash de contraseñas con BCrypt.
// La contraseña en claro nunca sale de esta clase ni se guarda:
// solo se persiste el hash con sal generado en el dispositivo.
// ==========================================
object Seguridad {

    // Costo del algoritmo (2^12 iteraciones): equilibrio razonable
    // entre resistencia a fuerza bruta y tiempos breves en móvil (RNF02).
    private const val COSTO = 12

    // Genera el hash BCrypt (con sal aleatoria incorporada) de una contraseña.
    fun hashPassword(password: String): String =
        BCrypt.withDefaults().hashToString(COSTO, password.toCharArray())

    // Verifica una contraseña contra un hash almacenado.
    // Devuelve true solo si coincide; nunca lanza excepción por hash corrupto.
    fun verificarPassword(password: String, hash: String): Boolean =
        BCrypt.verifyer().verify(password.toCharArray(), hash).verified
}
