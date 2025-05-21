package com.example.loginfuncional2.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.loginfuncional2.model.Turno

@Dao
interface TurnoDao {
    // En tu TurnoDao agrega:
    @Insert
    suspend fun insertarTurnos(turnos: List<Turno>)

    @Query("SELECT * FROM Turno WHERE fecha = :fecha AND disponible = 1")
    suspend fun obtenerHorariosDisponibles(fecha: String): List<Turno>

    @Query("UPDATE Turno SET disponible = 0 WHERE fecha = :fecha AND hora = :hora")
    suspend fun marcarTurnoComoNoDisponible(fecha: String, hora: String)

}
