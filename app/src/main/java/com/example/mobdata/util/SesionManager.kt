package com.example.mobdata.util

import android.content.Context
import androidx.core.content.edit
import com.example.mobdata.model.Credenciales

// ==========================================
// SESIÓN (RF01): recuerda al usuario que inició sesión en el dispositivo.
// Guarda el alias y el id de la cuenta, y una marca de expiración.
// No guarda la contraseña ni su hash: solo la identidad para no
// volver a pedir credenciales en cada arranque (RNF: usabilidad).
// ==========================================
object SesionManager {

    private const val PREFS = "LemacPrefs"
    private const val CLAVE_ALIAS = "alias"
    private const val CLAVE_ID = "usuarioId"
    private const val CLAVE_EXPIRA = "sesionExpira"

    // Duración de la sesión recordada: 30 días.
    private const val DURACION_MS = 30L * 24 * 60 * 60 * 1000

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // Guarda la sesión activa con su expiración.
    fun iniciar(context: Context, usuarioId: Long, alias: String) {
        prefs(context).edit {
            putString(CLAVE_ALIAS, alias)
            putLong(CLAVE_ID, usuarioId)
            putLong(CLAVE_EXPIRA, System.currentTimeMillis() + DURACION_MS)
        }
    }

    // Devuelve el alias con sesión vigente, o null si no hay o expiró.
    // Si expiró, limpia la sesión y borra los datos guardados.
    fun aliasActivo(context: Context): String? {
        val p = prefs(context)
        val alias = p.getString(CLAVE_ALIAS, null) ?: return null
        val expira = p.getLong(CLAVE_EXPIRA, 0L)
        if (!Credenciales.sesionVigente(expira, System.currentTimeMillis())) {
            cerrar(context)
            return null
        }
        return alias
    }

    fun idActivo(context: Context): Long = prefs(context).getLong(CLAVE_ID, 0L)

    // Cierra la sesión y elimina todos los datos guardados (logout robusto).
    fun cerrar(context: Context) {
        prefs(context).edit { clear() }
    }
}
