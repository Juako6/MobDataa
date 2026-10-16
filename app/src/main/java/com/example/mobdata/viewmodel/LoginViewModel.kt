package com.example.mobdata.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobdata.model.EstadoLogin
import com.example.mobdata.model.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ==========================================
// VIEWMODEL: estado y validaciones del login con alias ficticio
// ==========================================
class LoginViewModel : ViewModel() {

    private val _estado = MutableStateFlow(EstadoLogin())
    val estado: StateFlow<EstadoLogin> = _estado.asStateFlow()

    fun actualizarAlias(texto: String) {
        _estado.value = _estado.value.copy(alias = texto, mensajeError = null)
    }

    fun validarYIngresar(onExito: (String) -> Unit) {
        val error = Validaciones.validarAlias(_estado.value.alias)
        if (error != null) {
            _estado.value = _estado.value.copy(mensajeError = error)
            return
        }
        onExito(_estado.value.alias.trim())
    }
}
