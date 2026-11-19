package com.example.mobdata.data.remoto

// ==========================================
// REPOSITORY remoto (Retrofit) para la funcionalidad "lista de usuarios".
// Única puerta de acceso a la API REST desde la capa de UI:
//
//   Compose -> ViewModel -> UsuariosRemotosRepository -> UsuariosApiService -> Retrofit -> API
//
// Mantiene la misma idea que el resto de repositorios de la app
// (UsuarioRepository, RegistroRepository): la UI nunca toca el servicio
// directamente. Las excepciones de red se propagan hacia arriba para que el
// ViewModel las traduzca al estado Error de la interfaz.
// ==========================================
class UsuariosRemotosRepository(
    private val api: UsuariosApiService = RetrofitClient.usuariosApiService
) {

    // Llama al endpoint GET /usuarios y desen vuelve solo la lista de la envolvente.
    // `suspend` porque Retrofit ejecuta la petición de forma asíncrona.
    suspend fun obtenerUsuarios(): List<UsuarioApi> =
        api.listarUsuarios().data
}