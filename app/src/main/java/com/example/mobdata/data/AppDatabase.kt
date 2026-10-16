package com.example.mobdata.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ==========================================
// BASE DE DATOS Room (singleton) — persistencia local en el dispositivo
// ==========================================
@Database(entities = [RegistroAnimo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun registroAnimoDao(): RegistroAnimoDao

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lemacdata.db"
                ).build().also { INSTANCIA = it }
            }
    }
}
