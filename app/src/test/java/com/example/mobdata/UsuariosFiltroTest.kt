package com.example.mobdata

import com.example.mobdata.data.remoto.UsuarioApi
import com.example.mobdata.model.ResumenUsuarios
import com.example.mobdata.model.UsuariosFiltro
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UsuariosFiltroTest {

    // Fixture reutilizable: 2 activos + 1 inactivo
    private val usuariosEjemplo = listOf(
        UsuarioApi(
            1, "dra.lopez", "Dra. María López",
            "maria.lopez@mobdata.health", "psicologa", true,
            "2025-09-12T10:24:00Z"
        ),
        UsuarioApi(
            2, "lic.ruiz", "Lic. Carlos Ruiz",
            "carlos.ruiz@mobdata.health", "terapeuta", true,
            "2025-09-20T15:05:00Z"
        ),
        UsuarioApi(
            3, "coord.silva", "Andrea Silva",
            "andrea.silva@mobdata.health", "coordinadora", false,
            "2025-10-03T08:42:00Z"
        )
    )

    // PRUEBA 1 (caso válido)
    @Test
    fun soloActivosFiltraYDevuelveSoloLosActivos() {
        // Entrada conocida (Arrange)
        val entrada = usuariosEjemplo

        // Ejecución (Act)
        val resultado = UsuariosFiltro.soloActivos(entrada)

        // Resultado esperado (Assert)
        assertEquals(2, resultado.size)
        assertTrue(resultado.all { it.activo })
    }

    // PRUEBA 2 (caso inválido / borde: lista vacía)
    @Test
    fun soloActivosConListaVaciaDevuelveListaVacia() {
        // Entrada conocida (Arrange)
        val entrada = emptyList<UsuarioApi>()

        // Ejecución (Act)
        val resultado = UsuariosFiltro.soloActivos(entrada)

        // Resultado esperado (Assert)
        assertEquals(0, resultado.size)
    }

    // PRUEBA 3 (caso borde: nulo)
    @Test
    fun soloActivosConNuloDevuelveListaVacia() {
        // Entrada conocida (Arrange)
        val entrada: List<UsuarioApi>? = null

        // Ejecución (Act)
        val resultado = UsuariosFiltro.soloActivos(entrada)

        // Resultado esperado (Assert)
        assertEquals(0, resultado.size)
    }

    // PRUEBA 4 (caso válido: búsqueda insensible a mayúsculas)
    @Test
    fun buscarPorTextoEncuentraCoincidenciasInsensiblesAMayusculas() {
        // Entrada conocida (Arrange)
        val entrada = usuariosEjemplo
        val textoBuscado = "LOPEZ"

        // Ejecución (Act)
        val resultado = UsuariosFiltro.buscar(entrada, textoBuscado)

        // Resultado esperado (Assert)
        assertEquals(1, resultado.size)
        assertEquals("dra.lopez", resultado[0].alias)
    }

    // PRUEBA 5 (caso inválido: texto vacío o blancos)
    @Test
    fun buscarConTextoVacioOBlancosDevuelveListaVacia() {
        // Entrada conocida (Arrange)
        val entrada = usuariosEjemplo

        // Ejecución (Act)
        val resultadoVacio = UsuariosFiltro.buscar(entrada, "")
        val resultadoBlancos = UsuariosFiltro.buscar(entrada, " ")

        // Resultado esperado (Assert)
        assertEquals(0, resultadoVacio.size)
        assertEquals(0, resultadoBlancos.size)
    }

    // PRUEBA 6 (caso válido: resumen cuenta correctamente)
    @Test
    fun resumenActivosCuentaCorrectamenteActivosEInactivos() {
        // Entrada conocida (Arrange)
        val entrada = usuariosEjemplo

        // Ejecución (Act)
        val resumen = UsuariosFiltro.resumenActivos(entrada)

        // Resultado esperado (Assert)
        assertEquals(ResumenUsuarios(3, 2, 1), resumen)
    }

    // PRUEBA 7 (caso borde: resumen con nulo)
    @Test
    fun resumenActivosConNuloDevuelveCerosSinExcepcion() {
        // Entrada conocida (Arrange)
        val entrada: List<UsuarioApi>? = null

        // Ejecución (Act)
        val resumen = UsuariosFiltro.resumenActivos(entrada)

        // Resultado esperado (Assert)
        assertEquals(0, resumen.total)
        assertEquals(0, resumen.activos)
        assertEquals(0, resumen.inactivos)
    }
}