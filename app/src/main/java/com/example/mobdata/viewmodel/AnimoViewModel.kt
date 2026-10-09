package com.example.mobdata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdata.data.AppDatabase
import com.example.mobdata.data.RegistroAnimo
import com.example.mobdata.model.EstadoFormularioAnimo
import com.example.mobdata.model.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ==========================================
// VIEWMODEL: estado, validaciones y guardado del check-in.
// Ahora persiste en Room: los registros sobreviven
// al cierre de la aplicación y se recuperan al volver a entrar.
// SCREEN -> evento -> VIEWMODEL -> validación -> Room -> estado -> SCREEN
// ==========================================
class AnimoViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.obtener(app).registroAnimoDao()

    private val _estadoFormulario = MutableStateFlow(EstadoFormularioAnimo())
    val estadoFormulario: StateFlow<EstadoFormularioAnimo> = _estadoFormulario.asStateFlow()

    // Historial reactivo: Room emite la lista cada vez que la tabla cambia,
    // así la UI se actualiza sola al insertar un registro.
    val historialRegistros: StateFlow<List<RegistroAnimo>> = dao.obtenerTodos().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

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

    // VALIDACIÓN Y GUARDADO EN LA BASE DE DATOS LOCAL
    fun guardarRegistro(onExito: () -> Unit) {
        val estadoActual = _estadoFormulario.value

        // Reglas del formulario (obligatorio + rango 1..5)
        val error = Validaciones.validarCheckIn(
            estadoActual.emocionSeleccionada,
            estadoActual.intensidad
        )
        if (error != null) {
            _estadoFormulario.value = estadoActual.copy(mensajeError = error)
            return
        }

        val nuevoRegistro = RegistroAnimo(
            emocion = estadoActual.emocionSeleccionada,
            intensidad = estadoActual.intensidad,
            nota = estadoActual.notaTexto.ifBlank { "Sin nota" }
        )

        // Persistencia local: inserción asíncrona en Room
        viewModelScope.launch {
            dao.insertar(nuevoRegistro)
        }

        // Limpiar formulario y activar confirmación visible
        _estadoFormulario.value = EstadoFormularioAnimo(guardadoExitoso = true)

        onExito()
    }

    fun ocultarBannerConfirmacion() {
        _estadoFormulario.value = _estadoFormulario.value.copy(guardadoExitoso = false)
    }
}
