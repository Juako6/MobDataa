package com.example.mobdata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdata.data.AppDatabase
import com.example.mobdata.data.ContenidoCatalogo
import com.example.mobdata.data.RegistroDbt
import com.example.mobdata.data.RegistroRepository
import com.example.mobdata.model.EstadoFormularioDbt
import com.example.mobdata.model.Validaciones
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ==========================================
// VIEWMODEL (RF03): diario de habilidades DBT (crear y editar).
// La habilidad se elige desde el catálogo guardado en Room.
// ==========================================
class DbtViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = RegistroRepository(AppDatabase.obtener(app))

    private val _estadoFormulario = MutableStateFlow(EstadoFormularioDbt())
    val estadoFormulario: StateFlow<EstadoFormularioDbt> = _estadoFormulario.asStateFlow()

    // Catálogo de habilidades (reactivo)
    val habilidades: StateFlow<List<ContenidoCatalogo>> = repositorio.habilidadesDbt.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch { repositorio.asegurarCatalogoInicial() }
    }

    fun iniciarNuevo() {
        _estadoFormulario.value = EstadoFormularioDbt()
    }

    fun cargarParaEditar(id: Long) {
        _estadoFormulario.value = EstadoFormularioDbt(idEdicion = id)
        viewModelScope.launch {
            val r = repositorio.obtenerDbt(id) ?: return@launch
            _estadoFormulario.value = EstadoFormularioDbt(
                habilidad = r.habilidad,
                categoria = r.categoria,
                situacion = r.situacion,
                intensidad = r.intensidad,
                utilidad = r.utilidad,
                idEdicion = r.id,
                fechaOriginal = r.fecha
            )
        }
    }

    private fun modificar(cambio: (EstadoFormularioDbt) -> EstadoFormularioDbt) {
        _estadoFormulario.value = cambio(_estadoFormulario.value)
            .copy(mensajeError = null, guardadoExitoso = false)
    }

    fun seleccionarHabilidad(habilidad: ContenidoCatalogo) =
        modificar { it.copy(habilidad = habilidad.titulo, categoria = habilidad.categoria) }

    fun actualizarSituacion(texto: String) = modificar { it.copy(situacion = texto) }
    fun seleccionarIntensidad(nivel: Int) = modificar { it.copy(intensidad = nivel) }
    fun seleccionarUtilidad(nivel: Int) = modificar { it.copy(utilidad = nivel) }

    fun guardarRegistro(onExito: () -> Unit) {
        val e = _estadoFormulario.value

        val error = Validaciones.validarDbt(e.habilidad, e.situacion, e.intensidad, e.utilidad)
        if (error != null) {
            _estadoFormulario.value = e.copy(mensajeError = error)
            return
        }

        viewModelScope.launch {
            try {
                if (e.modoEdicion) {
                    repositorio.actualizarDbt(
                        RegistroDbt(
                            id = e.idEdicion,
                            habilidad = e.habilidad,
                            categoria = e.categoria,
                            situacion = e.situacion.trim(),
                            intensidad = e.intensidad,
                            utilidad = e.utilidad,
                            fecha = e.fechaOriginal
                        )
                    )
                    _estadoFormulario.value = e.copy(guardadoExitoso = true, mensajeError = null)
                } else {
                    repositorio.insertarDbt(
                        RegistroDbt(
                            habilidad = e.habilidad,
                            categoria = e.categoria,
                            situacion = e.situacion.trim(),
                            intensidad = e.intensidad,
                            utilidad = e.utilidad
                        )
                    )
                    _estadoFormulario.value = EstadoFormularioDbt(guardadoExitoso = true)
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
