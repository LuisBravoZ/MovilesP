package com.example.loginfuncional2.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.loginfuncional2.dao.CitaDao
import com.example.loginfuncional2.dao.TurnoDao
import com.example.loginfuncional2.dao.UsuarioDao
import com.example.loginfuncional2.model.Cita
import com.example.loginfuncional2.model.Turno
import com.example.loginfuncional2.model.Usuario

@Database(entities = [Usuario::class, Cita::class, Turno ::class], version = 11, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun usuarioDao(): UsuarioDao //metdo adbtracto que permite acceder a las operaciones definidas en un usuario dao
    abstract fun citaDao(): CitaDao
    abstract fun turnoDao(): TurnoDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tierra.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


}