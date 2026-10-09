package com.example.mobdata.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobdata.model.EstadoFormularioAnimo
import com.example.mobdata.model.RegistroAnimo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ==========================================
// VIEWMODEL: lógica de validación y estado del check-in de ánimo
// SCREEN -> evento -> VIEWMODEL -> validación -> nuevo estado -> SCREEN
// ==========================================
class AnimoViewModel : ViewModel() {

    private val _estadoFormulario = MutableStateFlow(EstadoFormularioAnimo())
    val estadoFormulario: StateFlow<EstadoFormularioAnimo> = _estadoFormulario.asStateFlow()

    private val _historialRegistros = MutableStateFlow<List<RegistroAnimo>>(emptyList())
    val historialRegistros: StateFlow<List<RegistroAnimo>> = _historialRegistros.asStateFlow()

    fun seleccionarEmocion(emocion: String) {
        _estadoFormulario.value = _estadoFormulario.value.copy(
            emocionSeleccionada = emocion,
            mensajeError = null
        )
    }

    fun seleccionarIntensidad(nivel: Int) {
        _estadoFormulario.value = _estadoFormulario.value.copy(
            intensidad = nivel,
            mensajeError = null
        )
    }

    fun actualizarNota(texto: String) {
        _estadoFormulario.value = _estadoFormulario.value.copy(notaTexto = texto)
    }

    // VALIDACIÓN Y GUARDADO DEL REGISTRO
    fun guardarRegistro(onExito: () -> Unit) {
        val estadoActual = _estadoFormulario.value

        // REGLA 1 (campo obligatorio): debe seleccionar una emoción
        if (estadoActual.emocionSeleccionada.isEmpty()) {
            _estadoFormulario.value = estadoActual.copy(
                mensajeError = "Debes seleccionar una emoción."
            )
            return
        }

        // REGLA 2 (rango): debe seleccionar una intensidad entre 1 y 5
        if (estadoActual.intensidad < 1 || estadoActual.intensidad > 5) {
            _estadoFormulario.value = estadoActual.copy(
                mensajeError = "Selecciona un nivel de intensidad (1 al 5)."
            )
            return
        }

        val nuevoRegistro = RegistroAnimo(
            emocion = estadoActual.emocionSeleccionada,
            intensidad = estadoActual.intensidad,
            nota = estadoActual.notaTexto.ifBlank { "Sin nota" },
            fechaHora = "Hoy - " + java.text.SimpleDateFormat(
                "HH:mm", java.util.Locale.getDefault()
            ).format(java.util.Date())
        )

        _historialRegistros.value = listOf(nuevoRegistro) + _historialRegistros.value

        // Limpiar formulario y activar confirmación visible
        _estadoFormulario.value = EstadoFormularioAnimo(guardadoExitoso = true)

        onExito()
    }

    fun buscarPorId(id: Long): RegistroAnimo? =
        _historialRegistros.value.find { it.id == id }

    fun ocultarBannerConfirmacion() {
        _estadoFormulario.value = _estadoFormulario.value.copy(guardadoExitoso = false)
    }
}
