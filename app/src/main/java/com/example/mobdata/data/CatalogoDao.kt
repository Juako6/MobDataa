package com.example.mobdata.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// DAO CRUD del catálogo de contenidos (RF10).
// La app lo usa en modo lectura; insertar/actualizar/eliminar quedan
// disponibles para el perfil "responsable de contenidos".
@Dao
interface CatalogoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(contenidos: List<ContenidoCatalogo>)

    @Query("SELECT * FROM catalogo_contenidos ORDER BY tipo, categoria, titulo")
    fun obtenerTodos(): Flow<List<ContenidoCatalogo>>

    @Query("SELECT * FROM catalogo_contenidos WHERE tipo = :tipo ORDER BY categoria, titulo")
    fun obtenerPorTipo(tipo: String): Flow<List<ContenidoCatalogo>>

    @Query("SELECT COUNT(*) FROM catalogo_contenidos")
    suspend fun contar(): Int

    @Update
    suspend fun actualizar(contenido: ContenidoCatalogo)

    @Delete
    suspend fun eliminar(contenido: ContenidoCatalogo)
}
