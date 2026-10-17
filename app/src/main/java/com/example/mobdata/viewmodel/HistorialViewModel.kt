package com.example.mobdata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobdata.data.AppDatabase
import com.example.mobdata.data.RegistroRepository
import com.example.mobdata.model.Ambito
import com.example.mobdata.model.ItemHistorial
import com.example.mobdata.model.aItemHistorial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ==========================================
// VIEWMODEL (RF04): historial unificado, filtro por ámbito y eliminación.
// Los listados son null mientras Room no ha emitido la primera lectura.
// ==========================================
class HistorialViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = RegistroRepository(AppDatabase.obtener(app))

    private val _filtro = MutableStateFlow<Ambito?>(null)
    val filtro: StateFlow<Ambito?> = _filtro.asStateFlow()

    // Todos los registros de todos los ámbitos, del más reciente al más antiguo
    val todos: StateFlow<List<ItemHistorial>?> = combine(
        repositorio.animos,
        repositorio.neurodesarrollo,
        repositorio.dbt
    ) { animos, neuros, dbts ->
        (animos.map { it.aItemHistorial() } +
            neuros.map { it.aItemHistorial() } +
            dbts.map { it.aItemHistorial() })
            .sortedByDescending { it.fecha }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    // Lista visible según el filtro seleccionado
    val registros: StateFlow<List<ItemHistorial>?> = combine(todos, _filtro) { lista, filtro ->
        if (filtro == null) lista else lista?.filter { it.ambito == filtro }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun filtrarPor(ambito: Ambito?) {
        _filtro.value = ambito
    }

    // DELETE en Room según el ámbito del registro
    fun eliminar(ambito: Ambito, id: Long) {
        viewModelScope.launch {
            when (ambito) {
                Ambito.ANIMO -> repositorio.eliminarAnimo(id)
                Ambito.NEURODESARROLLO -> repositorio.eliminarNeurodesarrollo(id)
                Ambito.DBT -> repositorio.eliminarDbt(id)
            }
        }
    }
}
