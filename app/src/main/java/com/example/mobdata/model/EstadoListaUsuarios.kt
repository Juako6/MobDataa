package com.example.mobdata.model

import com.example.mobdata.data.remoto.UsuarioApi

// ==========================================
// ESTADOS de la pantalla "Lista de usuarios (API REST)".
//
// Sealed interface: el `when` que la consume en Compose es EXHAUSTIVO, es decir,
// el compilador obliga a cubrir los tres casos (Cargando, Exito, Error).
// Es la base del patrón StateFlow de un solo estado que reacciona a la red.
// ==========================================
sealed interface EstadoListaUsuarios {

    // Estado inicial y mientras la petición HTTP está en curso (spinner).
    data object Cargando : EstadoListaUsuarios

    // La API respondió correctamente: la lista viaja DENTRO del estado.
    data class Exito(val usuarios: List<UsuarioApi>) : EstadoListaUsuarios

    // Fallo de red o del servidor: mensaje listo para mostrar en la UI.
    data class Error(val mensaje: String) : EstadoListaUsuarios
}