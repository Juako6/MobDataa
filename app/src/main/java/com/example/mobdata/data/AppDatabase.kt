package com.example.mobdata.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// ==========================================
// BASE DE DATOS Room (singleton) — persistencia local en el dispositivo
// v1: registros_animo
// v2: + sincronizado en registros_animo, registros_neurodesarrollo,
//       registros_dbt y catalogo_contenidos
// ==========================================
@Database(
    entities = [
        RegistroAnimo::class,
        RegistroNeurodesarrollo::class,
        RegistroDbt::class,
        ContenidoCatalogo::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun registroAnimoDao(): RegistroAnimoDao
    abstract fun registroNeurodesarrolloDao(): RegistroNeurodesarrolloDao
    abstract fun registroDbtDao(): RegistroDbtDao
    abstract fun catalogoDao(): CatalogoDao

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        // Migración 1 -> 2: conserva los registros de ánimo ya guardados
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE registros_animo ADD COLUMN sincronizado INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS registros_neurodesarrollo (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "rutina TEXT NOT NULL, " +
                        "organizacionTareas TEXT NOT NULL, " +
                        "tipoEpisodio TEXT NOT NULL, " +
                        "estrategia TEXT NOT NULL, " +
                        "resultado INTEGER NOT NULL, " +
                        "fecha INTEGER NOT NULL, " +
                        "sincronizado INTEGER NOT NULL DEFAULT 0)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS registros_dbt (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "habilidad TEXT NOT NULL, " +
                        "categoria TEXT NOT NULL, " +
                        "situacion TEXT NOT NULL, " +
                        "intensidad INTEGER NOT NULL, " +
                        "utilidad INTEGER NOT NULL, " +
                        "fecha INTEGER NOT NULL, " +
                        "sincronizado INTEGER NOT NULL DEFAULT 0)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS catalogo_contenidos (" +
                        "id INTEGER PRIMARY KEY NOT NULL, " +
                        "tipo TEXT NOT NULL, " +
                        "categoria TEXT NOT NULL, " +
                        "titulo TEXT NOT NULL, " +
                        "descripcion TEXT NOT NULL)"
                )
            }
        }

        fun obtener(context: Context): AppDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lemacdata.db"
                )
                    .addMigrations(MIGRACION_1_2)
                    .build()
                    .also { INSTANCIA = it }
            }
    }
}
