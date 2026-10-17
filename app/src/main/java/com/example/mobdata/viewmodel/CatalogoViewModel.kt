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
}
