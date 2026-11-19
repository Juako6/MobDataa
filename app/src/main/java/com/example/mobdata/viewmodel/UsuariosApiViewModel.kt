package com.example.mobdata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdata.data.remoto.UsuariosRemotosRepository
import com.example.mobdata.model.EstadoListaUsuarios
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// ==========================================
// VIEWMODEL de la funcionalidad "lista de usuarios" (API REST).
// Expone un ÚNICO StateFlow<EstadoListaUsuarios> que la pantalla Compose
// observa. Lanza la carga en init() y también con reintentar().
//
// Arquitectura:  Compose -> ViewModel -> Repository -> ApiService -> Retrofit -> API
// ==========================================
class UsuariosApiViewModel(
    private val repositorio: UsuariosRemotosRepository = UsuariosRemotosRepository()
) : ViewModel() {

    // Backing mutable privado + exposición pública de solo lectura (StateFlow).
    private val _estado = MutableStateFlow<EstadoListaUsuarios>(EstadoListaUsuarios.Cargando)
    val estado: StateFlow<EstadoListaUsuarios> = _estado.asStateFlow()

    init {
        // Nada más crear el ViewModel, disparamos la primera carga.
        cargarUsuarios()
    }

    // Carga (y recarga) la lista: pasa a Cargando, ejecuta la petición en una
    // corrutina del viewModelScope y traduce el resultado a Exito o Error.
    fun cargarUsuarios() {
        _estado.value = EstadoListaUsuarios.Cargando
        viewModelScope.launch {
            _estado.value = try {
                EstadoListaUsuarios.Exito(repositorio.obtenerUsuarios())
            } catch (e: Exception) {
                EstadoListaUsuarios.Error(mensajeError(e))
            }
        }
    }

    // Traduce la excepción técnica a un mensaje comprensible para el usuario.
    private fun mensajeError(e: Exception): String = when (e) {
        is UnknownHostException ->
            "Sin conexión al servidor. Revisa tu red e inténtalo de nuevo."
        is SocketTimeoutException ->
            "El servidor tardó demasiado en responder. Inténtalo de nuevo."
        else ->
            "No se pudo cargar la lista de usuarios: ${e.message ?: "error desconocido"}"
    }
}