package com.example.mobdata.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobdata.model.EstadoLogin
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
        val alias = _estado.value.alias.trim()

        // REGLA 1 (campo obligatorio)
        if (alias.isEmpty()) {
            _estado.value = _estado.value.copy(mensajeError = "El alias es obligatorio.")
            return
        }

        // REGLA 2 (longitud mínima)
        if (alias.length < 3) {
            _estado.value = _estado.value.copy(
                mensajeError = "El alias debe tener al menos 3 caracteres."
            )
            return
        }

        onExito(alias)
    }
}
