package com.example.mobdata

import com.example.mobdata.model.Credenciales
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// ==========================================
// PRUEBAS UNITARIAS de las reglas puras de credenciales y sesión (JVM puro, RF01)
// Ejecutar con: ./gradlew testDebugUnitTest
// ==========================================
class CredencialesTest {

    // ---- Registro: alias duplicado ----

    @Test
    fun insercionConAliasDuplicadoDevuelveMensajeDeOcupado() {
        assertEquals(Credenciales.MSG_ALIAS_OCUPADO, Credenciales.resultadoRegistro(-1L))
    }

    @Test
    fun insercionExitosaNoDevuelveError() {
        assertNull(Credenciales.resultadoRegistro(1L))
        assertNull(Credenciales.resultadoRegistro(42L))
    }

    // ---- Mensaje único de autenticación ----

    @Test
    fun mensajeDeCredencialesNoRevelaSiElAliasExiste() {
        // Un único mensaje para alias inexistente o contraseña incorrecta
        assertNotNull(Credenciales.MSG_CREDENCIALES_INVALIDAS)
        assertFalse(Credenciales.MSG_CREDENCIALES_INVALIDAS.contains("alias"))
    }

    // ---- Vigencia de la sesión ----

    @Test
    fun sesionSinExpiracionNoEsVigente() {
        assertFalse(Credenciales.sesionVigente(expira = 0L, ahora = 1_000L))
    }

    @Test
    fun sesionAntesDelVencimientoEsVigente() {
        assertTrue(Credenciales.sesionVigente(expira = 2_000L, ahora = 1_000L))
    }

    @Test
    fun sesionEnElInstanteDelVencimientoSigueVigente() {
        assertTrue(Credenciales.sesionVigente(expira = 1_000L, ahora = 1_000L))
    }

    @Test
    fun sesionDespuesDelVencimientoYaNoEsVigente() {
        assertFalse(Credenciales.sesionVigente(expira = 1_000L, ahora = 1_001L))
    }
}
