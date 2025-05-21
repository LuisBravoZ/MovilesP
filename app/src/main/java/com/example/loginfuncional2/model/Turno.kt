package com.example.loginfuncional2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Turno")
data class Turno(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fecha: String, // formato: "2025-05-21"
    val hora: String,  // formato: "09:00", "10:00", etc.
    val disponible: Boolean = true
)
