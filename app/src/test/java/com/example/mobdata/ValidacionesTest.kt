package com.example.mobdata

import com.example.mobdata.model.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// ==========================================
// PRUEBAS UNITARIAS de las reglas de validación (JVM puro)
// Ejecutar con: ./gradlew testDebugUnitTest
// ==========================================
class ValidacionesTest {

    // ---- Estado de ánimo ----

    @Test
    fun checkInSinEmocionDevuelveErrorDeObligatorio() {
        assertEquals("Debes seleccionar una emoción.", Validaciones.validarCheckIn("", 3))
    }

    @Test
    fun checkInSinIntensidadDevuelveErrorDeRango() {
        assertEquals(
            "Selecciona un nivel de intensidad (1 al 5).",
            Validaciones.validarCheckIn("Calma", 0)
        )
    }

    @Test
    fun checkInConIntensidadFueraDeRangoDevuelveError() {
        assertNotNull(Validaciones.validarCheckIn("Calma", 6))
    }

    @Test
    fun checkInValidoNoDevuelveError() {
        assertNull(Validaciones.validarCheckIn("Calma", 4))
    }

    @Test
    fun checkInConNotaConDatosPersonalesDevuelveError() {
        assertNotNull(Validaciones.validarCheckIn("Calma", 4, "escribeme a juan@correo.cl"))
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
    fun aliasMuyLargoDevuelveError() {
        assertNotNull(Validaciones.validarAlias("a".repeat(21)))
    }

    @Test
    fun aliasValidoNoDevuelveError() {
        assertNull(Validaciones.validarAlias("Usuario_Sintetico_01"))
    }

    @Test
    fun aliasConEspaciosDevuelveError() {
        assertNotNull(Validaciones.validarAlias("Juan Perez"))
    }

    @Test
    fun aliasQueParecenRutDevuelveError() {
        assertNotNull(Validaciones.validarAlias("12.345.678-9"))
        assertNotNull(Validaciones.validarAlias("12345678-K"))
    }

    // ---- RNF03: detección de datos personales ----

    @Test
    fun detectaCorreoRutYTelefono() {
        assertTrue(Validaciones.contieneDatosPersonales("mi correo es a@b.cl"))
        assertTrue(Validaciones.contieneDatosPersonales("rut 12.345.678-5"))
        assertTrue(Validaciones.contieneDatosPersonales("llamame al +56 9 1234 5678"))
    }

    @Test
    fun textoNormalNoSeMarcaComoDatoPersonal() {
        assertFalse(Validaciones.contieneDatosPersonales("Dormí 7 horas y estudié 2 bloques"))
    }

    // ---- RF02: Neurodesarrollo ----

    private fun neuroValido(
        rutina: String = "Rutina de mañana",
        tareas: String = "",
        episodio: String = "Sobrecarga",
        estrategia: String = "Pausa de cinco minutos",
        resultado: Int = 4
    ) = Validaciones.validarNeurodesarrollo(rutina, tareas, episodio, estrategia, resultado)

    @Test
    fun neuroValidoNoDevuelveError() {
        assertNull(neuroValido())
    }

    @Test
    fun neuroSinRutinaDevuelveError() {
        assertEquals("Falta completar: la rutina.", neuroValido(rutina = "  "))
    }

    @Test
    fun neuroSinEpisodioDevuelveError() {
        assertEquals("Selecciona el tipo de episodio.", neuroValido(episodio = ""))
    }

    @Test
    fun neuroSinEstrategiaDevuelveError() {
        assertNotNull(neuroValido(estrategia = ""))
    }

    @Test
    fun neuroConResultadoFueraDeRangoDevuelveError() {
        assertEquals("Indica el resultado percibido (1 al 5).", neuroValido(resultado = 0))
        assertNotNull(neuroValido(resultado = 7))
    }

    @Test
    fun neuroConTareasMuyLargasDevuelveError() {
        assertNotNull(neuroValido(tareas = "x".repeat(201)))
    }

    // ---- RF03: Habilidades DBT ----

    @Test
    fun dbtValidoNoDevuelveError() {
        assertNull(Validaciones.validarDbt("Observar", "Mucho ruido en la sala", 3, 4))
    }

    @Test
    fun dbtSinHabilidadDevuelveError() {
        assertEquals(
            "Selecciona la habilidad practicada del catálogo.",
            Validaciones.validarDbt("", "Mucho ruido", 3, 4)
        )
    }

    @Test
    fun dbtSinSituacionDevuelveError() {
        assertNotNull(Validaciones.validarDbt("Observar", "", 3, 4))
    }

    @Test
    fun dbtConEscalasInvalidasDevuelveError() {
        assertNotNull(Validaciones.validarDbt("Observar", "Mucho ruido", 0, 4))
        assertNotNull(Validaciones.validarDbt("Observar", "Mucho ruido", 3, 6))
    }
}
