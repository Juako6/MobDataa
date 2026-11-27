package com.example.mobdata.data.remoto

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Payload anónimo enviado al backend de prueba al sincronizar registros (RNF01 / RNF03).
 * No incluye alias ni datos personales.
 */
data class RegistroSyncPayload(
    val ambito: String,
    val fecha: Long,
    val resumen: String
)

interface RegistrosApiService {
    @POST
    suspend fun enviarRegistro(
        @Url url: String = "https://jsonplaceholder.typicode.com/posts",
        @Body payload: RegistroSyncPayload
    ): Response<Any>
}
