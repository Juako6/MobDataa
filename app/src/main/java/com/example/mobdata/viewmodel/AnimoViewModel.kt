package com.example.mobdata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdata.data.AppDatabase
import com.example.mobdata.data.RegistroAnimo
import com.example.mobdata.data.RegistroRepository
import com.example.mobdata.model.EstadoFormularioAnimo
import com.example.mobdata.model.Validaciones
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ==========================================
// VIEWMODEL: formulario de estado de ánimo (crear y editar).
// SCREEN -> evento -> VIEWMODEL -> validación -> Repository -> Room -> estado -> SCREEN
// ==========================================
class AnimoViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = RegistroRepository(AppDatabase.obtener(app))

    private val _estadoFormulario = MutableStateFlow(EstadoFormularioAnimo())
    val estadoFormulario: StateFlow<EstadoFormularioAnimo> = _estadoFormulario.asStateFlow()

    // Prepara el formulario vacío (registro nuevo)
    fun iniciarNuevo() {
        _estadoFormulario.value = EstadoFormularioAnimo()
    }

    // Carga un registro existente de Room para editarlo
    fun cargarParaEditar(id: Long) {
        _estadoFormulario.value = EstadoFormularioAnimo(idEdicion = id)
        viewModelScope.launch {
            val registro = repositorio.obtenerAnimo(id) ?: return@launch
            _estadoFormulario.value = EstadoFormularioAnimo(
                emocionSeleccionada = registro.emocion,
                intensidad = registro.intensidad,
                notaTexto = if (registro.nota == NOTA_VACIA) "" else registro.nota,
                idEdicion = registro.id,
                fechaOriginal = registro.fecha
            )
        }
    }

    fun seleccionarEmocion(emocion: String) {
        _estadoFormulario.value = _estadoFormulario.value.copy(
            emocionSeleccionada = emocion, mensajeError = null, guardadoExitoso = false
        )
    }

    fun seleccionarIntensidad(nivel: Int) {
        _estadoFormulario.value = _estadoFormulario.value.copy(
            intensidad = nivel, mensajeError = null, guardadoExitoso = false
        )
    }

    fun actualizarNota(texto: String) {
        _estadoFormulario.value = _estadoFormulario.value.copy(
            notaTexto = texto, mensajeError = null, guardadoExitoso = false
        )
    }

    // VALIDACIÓN Y GUARDADO (INSERT si es nuevo, UPDATE si se está editando)
    fun guardarRegistro(onExito: () -> Unit) {
        val e = _estadoFormulario.value

        val error = Validaciones.validarCheckIn(e.emocionSeleccionada, e.intensidad, e.notaTexto)
        if (error != null) {
            _estadoFormulario.value = e.copy(mensajeError = error)
            return
        }

        viewModelScope.launch {
            try {
                val nota = e.notaTexto.trim().ifBlank { NOTA_VACIA }
                if (e.modoEdicion) {
                    repositorio.actualizarAnimo(
                        RegistroAnimo(
                            id = e.idEdicion,
                            emocion = e.emocionSeleccionada,
                            intensidad = e.intensidad,
                            nota = nota,
                            fecha = e.fechaOriginal
                            // sincronizado = false: el cambio queda pendiente de sincronizar
                        )
                    )
                    _estadoFormulario.value = e.copy(guardadoExitoso = true, mensajeError = null)
                } else {
                    repositorio.insertarAnimo(
                        RegistroAnimo(
                            emocion = e.emocionSeleccionada,
                            intensidad = e.intensidad,
                            nota = nota
                        )
                    )
                    // Formulario limpio + confirmación visible
                    _estadoFormulario.value = EstadoFormularioAnimo(guardadoExitoso = true)
                }
                onExito()
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                _estadoFormulario.value = e.copy(
                    mensajeError = "No se pudo guardar el registro. Inténtalo nuevamente."
                )
            }
        }
    }

    fun ocultarBannerConfirmacion() {
        _estadoFormulario.value = _estadoFormulario.value.copy(guardadoExitoso = false)
    }

    private companion object {
        const val NOTA_VACIA = "Sin nota"
    }
}
