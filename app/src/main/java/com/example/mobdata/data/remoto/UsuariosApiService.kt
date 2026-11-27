package com.example.mobdata.data.remoto

import retrofit2.http.GET

// ==========================================
// ApiService: contrato de los endpoints REST de la funcionalidad.
// Cada método es `suspend` para poder llamarlo desde una corrutina
// (viewModelScope) sin bloquear el hilo principal.
//
// La ruta de @GET es RELATIVA ("c1480ce944701fd679fc"); se concatena con la baseUrl de
// RetrofitClient (https://api.npoint.io/), de modo que la URL real es:
//   https://api.npoint.io/c1480ce944701fd679fc
// Panel de administración de npoint:
//   https://www.npoint.io/docs/c1480ce944701fd679fc
// ==========================================
interface UsuariosApiService {

    // Lista todos los usuarios disponibles en el servidor.

    //pruebas
    //https://www.npoint.io/docs/c1480ce944701fd679fc

    @GET("c1480ce944701fd679fc")
    suspend fun listarUsuarios(): RespuestaUsuariosApi

    //@GET("usuarios")
    //suspend fun listarUsuarios(): RespuestaUsuariosApi
}