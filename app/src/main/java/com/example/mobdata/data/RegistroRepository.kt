package com.example.mobdata.data

import kotlinx.coroutines.flow.Flow

// ==========================================
// REPOSITORY: única puerta de acceso a los datos.
// La interfaz nunca toca los DAO directamente:
// Interfaz -> ViewModel -> Repository -> DAO -> Room -> SQLite
// ==========================================
class RegistroRepository(db: AppDatabase) {

    private val animoDao = db.registroAnimoDao()
    private val neuroDao = db.registroNeurodesarrolloDao()
    private val dbtDao = db.registroDbtDao()
    private val catalogoDao = db.catalogoDao()

    // ---------- READ (flujos reactivos) ----------
    val animos: Flow<List<RegistroAnimo>> = animoDao.obtenerTodos()
    val neurodesarrollo: Flow<List<RegistroNeurodesarrollo>> = neuroDao.obtenerTodos()
    val dbt: Flow<List<RegistroDbt>> = dbtDao.obtenerTodos()
    val catalogo: Flow<List<ContenidoCatalogo>> = catalogoDao.obtenerTodos()
    val habilidadesDbt: Flow<List<ContenidoCatalogo>> =
        catalogoDao.obtenerPorTipo(TiposContenido.HABILIDAD_DBT)

    // ---------- Estado de ánimo ----------
    suspend fun insertarAnimo(registro: RegistroAnimo): Long = animoDao.insertar(registro)
    suspend fun obtenerAnimo(id: Long): RegistroAnimo? = animoDao.obtenerPorId(id)
    suspend fun actualizarAnimo(registro: RegistroAnimo) = animoDao.actualizar(registro)
    suspend fun eliminarAnimo(id: Long) = animoDao.eliminarPorId(id)

    // ---------- Neurodesarrollo (RF02) ----------
    suspend fun insertarNeurodesarrollo(registro: RegistroNeurodesarrollo): Long =
        neuroDao.insertar(registro)
    suspend fun obtenerNeurodesarrollo(id: Long): RegistroNeurodesarrollo? =
        neuroDao.obtenerPorId(id)
    suspend fun actualizarNeurodesarrollo(registro: RegistroNeurodesarrollo) =
        neuroDao.actualizar(registro)
    suspend fun eliminarNeurodesarrollo(id: Long) = neuroDao.eliminarPorId(id)

    // ---------- Habilidades DBT (RF03) ----------
    suspend fun insertarDbt(registro: RegistroDbt): Long = dbtDao.insertar(registro)
    suspend fun obtenerDbt(id: Long): RegistroDbt? = dbtDao.obtenerPorId(id)
    suspend fun actualizarDbt(registro: RegistroDbt) = dbtDao.actualizar(registro)
    suspend fun eliminarDbt(id: Long) = dbtDao.eliminarPorId(id)

    // ---------- Catálogo (RF10) ----------
    // Carga el contenido inicial si la tabla está vacía (ids fijos => idempotente)
    suspend fun asegurarCatalogoInicial() {
        if (catalogoDao.contar() == 0) {
            catalogoDao.insertarTodos(CatalogoSemilla.contenidos)
        }
    }
}
