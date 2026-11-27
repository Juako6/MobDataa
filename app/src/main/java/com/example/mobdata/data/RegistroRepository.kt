package com.example.mobdata.data

import com.example.mobdata.data.remoto.RegistroSyncPayload
import com.example.mobdata.data.remoto.RetrofitClient
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

    suspend fun insertarContenido(contenido: ContenidoCatalogo): Long =
        catalogoDao.insertar(contenido)

    suspend fun actualizarContenido(contenido: ContenidoCatalogo) =
        catalogoDao.actualizar(contenido)

    suspend fun eliminarContenido(contenido: ContenidoCatalogo) =
        catalogoDao.eliminar(contenido)

    // ---------- Sincronización con API (RNF01) ----------
    suspend fun sincronizarPendientes(): Result<Int> = runCatching {
        val api = RetrofitClient.registrosApiService
        var sincronizados = 0

        for (animo in animoDao.obtenerPendientes()) {
            val resp = api.enviarRegistro(
                payload = RegistroSyncPayload(
                    ambito = "ANIMO",
                    fecha = animo.fecha,
                    resumen = "${animo.emocion} (${animo.intensidad}/5)"
                )
            )
            if (resp.isSuccessful) {
                animoDao.marcarSincronizado(animo.id)
                sincronizados++
            } else {
                error("Error HTTP ${resp.code()}")
            }
        }

        for (neuro in neuroDao.obtenerPendientes()) {
            val resp = api.enviarRegistro(
                payload = RegistroSyncPayload(
                    ambito = "NEURODESARROLLO",
                    fecha = neuro.fecha,
                    resumen = "Rutina: ${neuro.rutina}, Resultado: ${neuro.resultado}/5"
                )
            )
            if (resp.isSuccessful) {
                neuroDao.marcarSincronizado(neuro.id)
                sincronizados++
            } else {
                error("Error HTTP ${resp.code()}")
            }
        }

        for (dbt in dbtDao.obtenerPendientes()) {
            val resp = api.enviarRegistro(
                payload = RegistroSyncPayload(
                    ambito = "DBT",
                    fecha = dbt.fecha,
                    resumen = "${dbt.habilidad} (utilidad ${dbt.utilidad}/5)"
                )
            )
            if (resp.isSuccessful) {
                dbtDao.marcarSincronizado(dbt.id)
                sincronizados++
            } else {
                error("Error HTTP ${resp.code()}")
            }
        }

        sincronizados
    }
}
