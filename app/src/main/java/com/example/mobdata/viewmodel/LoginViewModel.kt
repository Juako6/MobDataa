package com.example.mobdata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdata.data.AppDatabase
import com.example.mobdata.data.Usuario
import com.example.mobdata.data.UsuarioRepository
import com.example.mobdata.model.Credenciales
import com.example.mobdata.model.EstadoLogin
import com.example.mobdata.model.Validaciones
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ==========================================
// VIEWMODEL: login y registro con alias + contraseña local (RF01).
// PANTALLA -> evento -> VIEWMODEL -> Validaciones -> Repository -> Room -> estado -> PANTALLA
// La contraseña en claro solo vive en este estado en memoria: nunca se
// guarda ni se registra en texto. El alias y el hash sí llegan al Repository.
// ==========================================
class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = UsuarioRepository(AppDatabase.obtener(app))

    private val _estado = MutableStateFlow(EstadoLogin())
    val estado: StateFlow<EstadoLogin> = _estado.asStateFlow()

    fun actualizarAlias(texto: String) {
        _estado.value = _estado.value.copy(alias = texto, mensajeError = null)
    }

    fun actualizarPassword(texto: String) {
        _estado.value = _estado.value.copy(password = texto, mensajeError = null)
    }

    fun actualizarConfirmacionPassword(texto: String) {
        _estado.value = _estado.value.copy(confirmacionPassword = texto, mensajeError = null)
    }

    // Alterna entre "iniciar sesión" y "crear cuenta" y limpia la contraseña
    // escrita para no reutilizarla por error en el otro modo.
    fun cambiarModo(esRegistro: Boolean) {
        _estado.value = _estado.value.copy(
            esRegistro = esRegistro,
            password = "",
            confirmacionPassword = "",
            mensajeError = null
        )
    }

    // Valida y ejecuta la acción del modo activo.
    // onExito recibe el Usuario autenticado (o recién registrado).
    fun enviar(onExito: (Usuario) -> Unit) {
        val e = _estado.value
        if (e.cargando) return

        // 1) Validaciones puras comunes
        val errorAlias = Validaciones.validarAlias(e.alias)
        if (errorAlias != null) {
            mostrarError(errorAlias)
            return
        }
        val errorPassword = Validaciones.validarPassword(e.password)
        if (errorPassword != null) {
            mostrarError(errorPassword)
            return
        }
        if (e.esRegistro) {
            val errorConfirmacion =
                Validaciones.validarConfirmacionPassword(e.password, e.confirmacionPassword)
            if (errorConfirmacion != null) {
                mostrarError(errorConfirmacion)
                return
            }
        }

        // 2) Acceso a datos (Room) fuera del hilo principal
        _estado.value = e.copy(cargando = true, mensajeError = null)
        viewModelScope.launch {
            try {
                if (e.esRegistro) {
                    val errorRegistro = repositorio.registrar(e.alias, e.password)
                    if (errorRegistro != null) {
                        mostrarError(errorRegistro)
                        return@launch
                    }
                    val usuario = repositorio.autenticar(e.alias, e.password)
                    if (usuario == null) {
                        mostrarError("La cuenta se creó, pero no se pudo iniciar sesión. Intenta nuevamente.")
                        return@launch
                    }
                    _estado.value = _estado.value.copy(cargando = false)
                    onExito(usuario)
                } else {
                    val usuario = repositorio.autenticar(e.alias, e.password)
                    if (usuario == null) {
                        mostrarError(Credenciales.MSG_CREDENCIALES_INVALIDAS)
                        return@launch
                    }
                    _estado.value = _estado.value.copy(cargando = false)
                    onExito(usuario)
                }
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                mostrarError("Ocurrió un error inesperado. Inténtalo nuevamente.")
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        _estado.value = _estado.value.copy(mensajeError = mensaje, cargando = false)
    }
}
