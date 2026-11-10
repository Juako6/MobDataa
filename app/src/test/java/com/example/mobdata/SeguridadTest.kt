package com.example.mobdata

import com.example.mobdata.util.Seguridad
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// ==========================================
// PRUEBAS UNITARIAS del hash de contraseñas (JVM puro, RF01)
// Verifican que BCrypt genera hash distinto por sal y que
// la verificación acepta la contraseña correcta y rechaza el resto.
// ==========================================
class SeguridadTest {

    @Test
    fun hashNoEsElTextoPlano() {
        val hash = Seguridad.hashPassword("claveSegura123")
        assertNotEquals("claveSegura123", hash)
    }

    @Test
    fun dosHashesDeLaMismaClaveSonDistintosPorLaSal() {
        val hash1 = Seguridad.hashPassword("claveSegura123")
        val hash2 = Seguridad.hashPassword("claveSegura123")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun verificaCorrectamenteLaClaveGuardada() {
        val hash = Seguridad.hashPassword("claveSegura123")
        assertTrue(Seguridad.verificarPassword("claveSegura123", hash))
    }

    @Test
    fun rechazaClaveIncorrecta() {
        val hash = Seguridad.hashPassword("claveSegura123")
        assertFalse(Seguridad.verificarPassword("claveSegura124", hash))
    }

    @Test
    fun rechazaClaveVacia() {
        val hash = Seguridad.hashPassword("claveSegura123")
        assertFalse(Seguridad.verificarPassword("", hash))
    }
}
