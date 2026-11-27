package com.example.mobdata

import com.example.mobdata.model.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Test

class BugPasswordTest {

    // PRUEBA QUE FALLA (rojo) con el código actual
    @Test
    fun passwordDePocosEspaciosDeberiaDecirSoloEspaciosNoLongitudMinima() {
        // Entrada conocida (Arrange): 3 espacios (< PASSWORD_MIN)
        val passwordSoloEspacios = "   "

        // Ejecución (Act)
        val resultado = Validaciones.validarPassword(passwordSoloEspacios)

        // Resultado esperado (Assert):
        // El código actual devuelve "La contraseña debe tener al menos 8 caracteres."
        // y la prueba FALLA en rojo. Tras la corrección pasa a verde.
        assertEquals(
            "La contraseña no puede ser solo espacios.",
            resultado
        )
    }

    // Segundo caso que también falla con el código actual
    @Test
    fun passwordDeMuchosEspaciosDeberiaDecirSoloEspaciosNoLongitudMaxima() {
        // Entrada conocida (Arrange): 70 espacios (> PASSWORD_MAX)
        val passwordSoloEspacios = " ".repeat(70)

        // Ejecución (Act)
        val resultado = Validaciones.validarPassword(passwordSoloEspacios)

        // Resultado esperado (Assert)
        assertEquals(
            "La contraseña no puede ser solo espacios.",
            resultado
        )
    }

    // Prueba de regresión: la contraseña válida sigue siendo válida
    @Test
    fun passwordValidaSigueSiendoValidaTrasCorreccion() {
        // Entrada conocida (Arrange)
        val passwordValida = "claveSegura123"

        // Ejecución (Act)
        val resultado = Validaciones.validarPassword(passwordValida)

        // Resultado esperado (Assert): null = sin error
        assertEquals(null, resultado)
    }

    // Prueba de regresión: la contraseña vacía sigue dando "obligatoria"
    @Test
    fun passwordVaciaSigueDiciendoObligatoria() {
        // Entrada conocida (Arrange)
        val passwordVacia = ""

        // Ejecución (Act)
        val resultado = Validaciones.validarPassword(passwordVacia)

        // Resultado esperado (Assert)
        assertEquals("La contraseña es obligatoria.", resultado)
    }
}