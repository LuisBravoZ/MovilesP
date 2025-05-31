package com.example.loginfuncional2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "turnos")
data class Turno(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idNutricionista: Int,
    val fecha: String,
    val diaSemana: String,
    val horaInicio: String,
    val horaFin: String,
    val disponible: Boolean = true,
    val idPaciente: Int? = null // Nuevo campo para asociar el turno a un paciente
)