package com.example.loginfuncional2.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.loginfuncional2.dao.UsuarioDao
import com.example.loginfuncional2.model.Usuario

@Database(entities = [Usuario::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun usuarioDao(): UsuarioDao //metdo adbtracto que permite acceder a las operaciones definidas en un usuario dao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tierra.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }


}