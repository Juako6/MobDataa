package com.example.mobdata

import com.example.mobdata.model.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// ==========================================
// PRUEBAS UNITARIAS de las reglas de validación (JVM puro)
// ==========================================
class ValidacionesTest {

    // ---- RF02: check-in de estado de ánimo ----

    @Test
    fun checkInSinEmocionDevuelveErrorDeObligatorio() {
        val error = Validaciones.validarCheckIn("", 3)
        assertNotNull(error)
        assertEquals("Debes seleccionar una emoción.", error)
    }

    @Test
    fun checkInSinIntensidadDevuelveErrorDeRango() {
        val error = Validaciones.validarCheckIn("Calma", 0)
        assertEquals("Selecciona un nivel de intensidad (1 al 5).", error)
    }

    @Test
    fun checkInConIntensidadFueraDeRangoDevuelveError() {
        assertNotNull(Validaciones.validarCheckIn("Calma", 6))
    }

    @Test
    fun checkInValidoNoDevuelveError() {
        assertNull(Validaciones.validarCheckIn("Calma", 4))
    }

    // ---- RF01: alias de ingreso ----

    @Test
    fun aliasVacioDevuelveErrorDeObligatorio() {
        assertEquals("El alias es obligatorio.", Validaciones.validarAlias("   "))
    }

    @Test
    fun aliasMuyCortoDevuelveErrorDeLongitud() {
        assertEquals(
            "El alias debe tener al menos 3 caracteres.",
            Validaciones.validarAlias("ab")
        )
    }

    @Test
    fun aliasValidoNoDevuelveError() {
        assertNull(Validaciones.validarAlias("Usuario_Sintetico_01"))
    }
}
