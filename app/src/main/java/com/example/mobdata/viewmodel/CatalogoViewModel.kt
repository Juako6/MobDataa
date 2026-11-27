package com.example.mobdata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdata.data.AppDatabase
import com.example.mobdata.data.ContenidoCatalogo
import com.example.mobdata.data.RegistroRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ==========================================
// VIEWMODEL (RF10): consulta del catálogo de contenidos
// (guías DBT y lecturas psicoeducativas guardadas en Room)
// ==========================================
class CatalogoViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = RegistroRepository(AppDatabase.obtener(app))

    val contenidos: StateFlow<List<ContenidoCatalogo>> = repositorio.catalogo.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch { repositorio.asegurarCatalogoInicial() }
    }

    fun agregarContenido(
        tipo: String,
        categoria: String,
        titulo: String,
        descripcion: String
    ) {
        val tituloLimpio = titulo.trim()
        val descLimpia = descripcion.trim()
        val catLimpia = categoria.trim().ifEmpty { "General" }
        if (tituloLimpio.isEmpty() || descLimpia.isEmpty()) return

        viewModelScope.launch {
            val nuevoId = (contenidos.value.maxOfOrNull { it.id } ?: 100L) + 1L
            repositorio.insertarContenido(
                ContenidoCatalogo(
                    id = nuevoId,
                    tipo = tipo,
                    categoria = catLimpia,
                    titulo = tituloLimpio,
                    descripcion = descLimpia
                )
            )
        }
    }

    fun actualizarContenido(contenido: ContenidoCatalogo) {
        if (contenido.titulo.isBlank() || contenido.descripcion.isBlank()) return
        viewModelScope.launch {
            repositorio.actualizarContenido(
                contenido.copy(
                    titulo = contenido.titulo.trim(),
                    categoria = contenido.categoria.trim().ifEmpty { "General" },
                    descripcion = contenido.descripcion.trim()
                )
            )
        }
    }

    fun eliminarContenido(contenido: ContenidoCatalogo) {
        viewModelScope.launch {
            repositorio.eliminarContenido(contenido)
        }
    }
}
