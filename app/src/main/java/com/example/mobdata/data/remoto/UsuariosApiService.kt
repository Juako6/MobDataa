package com.example.mobdata.data.remoto

import retrofit2.http.GET

// ==========================================
// ApiService: contrato de los endpoints REST de la funcionalidad.
// Cada método es `suspend` para poder llamarlo desde una corrutina
// (viewModelScope) sin bloquear el hilo principal.
//
// La ruta de @GET es RELATIVA ("usuarios"); se concatena con la baseUrl de
// RetrofitClient, de modo que la URL final es:
//   https://api.mobdata.example/v1/usuarios
// ==========================================
interface UsuariosApiService {

    // Lista todos los usuarios disponibles en el servidor.
    @GET("usuarios")
    suspend fun listarUsuarios(): RespuestaUsuariosApi
}