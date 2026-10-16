package com.example.mobdata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdata.data.AppDatabase
import com.example.mobdata.data.RegistroNeurodesarrollo
import com.example.mobdata.data.RegistroRepository
import com.example.mobdata.model.EstadoFormularioNeuro
import com.example.mobdata.model.Validaciones
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ==========================================
// VIEWMODEL (RF02): registro de Neurodesarrollo (crear y editar)
// ==========================================
class NeurodesarrolloViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = RegistroRepository(AppDatabase.obtener(app))

    private val _estadoFormulario = MutableStateFlow(EstadoFormularioNeuro())
    val estadoFormulario: StateFlow<EstadoFormularioNeuro> = _estadoFormulario.asStateFlow()

    fun iniciarNuevo() {
        _estadoFormulario.value = EstadoFormularioNeuro()
    }

    fun cargarParaEditar(id: Long) {
        _estadoFormulario.value = EstadoFormularioNeuro(idEdicion = id)
        viewModelScope.launch {
            val r = repositorio.obtenerNeurodesarrollo(id) ?: return@launch
            _estadoFormulario.value = EstadoFormularioNeuro(
                rutina = r.rutina,
                organizacionTareas = r.organizacionTareas,
                tipoEpisodio = r.tipoEpisodio,
                estrategia = r.estrategia,
                resultado = r.resultado,
                idEdicion = r.id,
                fechaOriginal = r.fecha
            )
        }
    }

    private fun modificar(cambio: (EstadoFormularioNeuro) -> EstadoFormularioNeuro) {
        _estadoFormulario.value = cambio(_estadoFormulario.value)
            .copy(mensajeError = null, guardadoExitoso = false)
    }

    fun actualizarRutina(texto: String) = modificar { it.copy(rutina = texto) }
    fun actualizarOrganizacion(texto: String) = modificar { it.copy(organizacionTareas = texto) }
    fun seleccionarEpisodio(tipo: String) = modificar { it.copy(tipoEpisodio = tipo) }
    fun actualizarEstrategia(texto: String) = modificar { it.copy(estrategia = texto) }
    fun seleccionarResultado(nivel: Int) = modificar { it.copy(resultado = nivel) }

    fun guardarRegistro(onExito: () -> Unit) {
        val e = _estadoFormulario.value

        val error = Validaciones.validarNeurodesarrollo(
            e.rutina, e.organizacionTareas, e.tipoEpisodio, e.estrategia, e.resultado
        )
        if (error != null) {
            _estadoFormulario.value = e.copy(mensajeError = error)
            return
        }

        viewModelScope.launch {
            try {
                if (e.modoEdicion) {
                    repositorio.actualizarNeurodesarrollo(
                        RegistroNeurodesarrollo(
                            id = e.idEdicion,
                            rutina = e.rutina.trim(),
                            organizacionTareas = e.organizacionTareas.trim(),
                            tipoEpisodio = e.tipoEpisodio,
                            estrategia = e.estrategia.trim(),
                            resultado = e.resultado,
                            fecha = e.fechaOriginal
                        )
                    )
                    _estadoFormulario.value = e.copy(guardadoExitoso = true, mensajeError = null)
                } else {
                    repositorio.insertarNeurodesarrollo(
                        RegistroNeurodesarrollo(
                            rutina = e.rutina.trim(),
                            organizacionTareas = e.organizacionTareas.trim(),
                            tipoEpisodio = e.tipoEpisodio,
                            estrategia = e.estrategia.trim(),
                            resultado = e.resultado
                        )
                    )
                    _estadoFormulario.value = EstadoFormularioNeuro(guardadoExitoso = true)
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
}
