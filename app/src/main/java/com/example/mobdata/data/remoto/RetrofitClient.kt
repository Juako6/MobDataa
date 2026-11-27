package com.example.mobdata.data.remoto

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// ==========================================
// RetrofitClient: configuración ÚNICA (singleton) del cliente HTTP.
// Centraliza baseUrl, timeouts, logging y el convertidor Gson, y expone
// el ApiService ya construido para que lo consuma el Repository.
//
// Flujo de creación (una sola vez, al primer acceso):
//   OkHttp + HttpLoggingInterceptor  ->  Retrofit + GsonConverterFactory  ->  ApiService
// ==========================================
object RetrofitClient {

    // URL base del servidor REST.
    // IMPORTANTE: debe terminar en '/' porque Retrofit concatena rutas relativas.
    // Sustituye esta dirección por la de tu backend real (local con emulator:
    //   http://10.0.2.2:8080/v1/  apunta al localhost de tu máquina).
    private const val BASE_URL = "https://api.npoint.io/"

    // Logger de red: vuelca cada petición y respuesta en Logcat (tag "OkHttp").
    // Nivel BODY = cabeceras + cuerpo completo. En producción usa NONE o BASIC.
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente OkHttp con timeouts explícitos y el interceptor de logging.

    private val cliente = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia Retrofit reutilizable (singleton por el `object`).
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Implementación del ApiService creada de forma perezosa (lazy):
    // se construye solo la primera vez que alguien la pide.
    val usuariosApiService: UsuariosApiService by lazy {
        retrofit.create(UsuariosApiService::class.java)
    }

    val registrosApiService: RegistrosApiService by lazy {
        retrofit.create(RegistrosApiService::class.java)
    }
}