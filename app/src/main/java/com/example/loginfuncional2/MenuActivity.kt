package com.example.loginfuncional2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginfuncional2.database.AppDatabase
import com.example.loginfuncional2.model.Cita
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var rvUsuarios: RecyclerView
    private lateinit var usuarioAdapter: UsuarioAdapter
    private lateinit var tvUsuarios: TextView
    private lateinit var rvMisCitas: RecyclerView
    private lateinit var citaAdapter: CitaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()
        rvMisCitas = findViewById(R.id.rvmisCitas)
        rvMisCitas.layoutManager = LinearLayoutManager(this)
        mostrarMisCitas(userId)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val tvFechaSeleccionada = findViewById<TextView>(R.id.tvFechaSeleccionada)
        var fechaSeleccionada: String = ""

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val fechaSeleccionada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            //tvFechaSeleccionada.text = "Fecha seleccionada: $fechaSeleccionada"

            if (userId != -1) {
                mostrarHorariosDisponibles(userId, fechaSeleccionada)
            } else {
                Toast.makeText(this, "Error: usuario no identificado", Toast.LENGTH_SHORT).show()
            }
        }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logout()
        }
        val tvGoMenu = findViewById<TextView>(R.id.tv_go_to_atrasmenu)
        tvGoMenu.setOnClickListener {
            goToMenu()
        }
        //tvUsuarios = findViewById(R.id.tvUsuarios)
        rvUsuarios = findViewById(R.id.rvUsuarios)
        rvUsuarios.layoutManager = LinearLayoutManager(this)
        mostrarUsuarios()
    }



    private fun mostrarUsuarios() {
        val db = AppDatabase.getDatabase(this)
        val usuarioDao = db.usuarioDao()

        lifecycleScope.launch {
            usuarioDao.obtenerusuarios().collect { lista ->
                usuarioAdapter = UsuarioAdapter(lista)
                rvUsuarios.adapter = usuarioAdapter
            }
        }
    }

    private fun goToMenu() {
        val m = Intent(this, MainActivity::class.java)
        startActivity(m)
    }

    private fun reservarCita(usuarioId: Int, fecha: String, hora: String) {
        val db = AppDatabase.getDatabase(this)
        val citaDao = db.citaDao()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val citaExistente = citaDao.obtenerCitaPorFechaYHora(fecha, hora)

                withContext(Dispatchers.Main) {
                    if (citaExistente != null) {
                        Toast.makeText(
                            this@MenuActivity,
                            "Ya hay una cita reservada para el $fecha a las $hora",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Insertar la cita en hilo de fondo
                        withContext(Dispatchers.IO) {
                            citaDao.insertarCita(Cita(usuarioId = usuarioId, fecha = fecha, hora = hora))
                        }

                        Toast.makeText(
                            this@MenuActivity,
                            "Cita reservada para el $fecha a las $hora",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MenuActivity,
                        "Error al reservar la cita: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun logout() {
        val sessionManager = SessionManager(this)
        sessionManager.clearToken() // Elimina la sesión

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    private fun mostrarHorariosDisponibles(usuarioId: Int, fecha: String) {
        val db = AppDatabase.getDatabase(this)
        val turnoDao = db.turnoDao()

        lifecycleScope.launch {
            var horarios = turnoDao.obtenerHorariosDisponibles(fecha)
            if (horarios.isEmpty()) {
                // Insertar horarios predeterminados si no existen para la fecha seleccionada
                val horas = listOf("09:00", "10:00", "11:00", "12:00") // Puedes agregar más horarios
                val turnos = horas.map { hora ->
                    com.example.loginfuncional2.model.Turno(fecha = fecha, hora = hora, disponible = true)
                }
                turnoDao.insertarTurnos(turnos)
                // Volver a consultar después de insertar
                horarios = turnoDao.obtenerHorariosDisponibles(fecha)
            }

            if (horarios.isEmpty()) {
                Toast.makeText(this@MenuActivity, "No hay horarios disponibles para $fecha", Toast.LENGTH_SHORT).show()
            } else {
                val horas = horarios.map { it.hora }.toTypedArray()
                androidx.appcompat.app.AlertDialog.Builder(this@MenuActivity)
                    .setTitle("Selecciona un horario")
                    .setItems(horas) { _, which ->
                        val horaSeleccionada = horas[which]
                        reservarCita(usuarioId, fecha, horaSeleccionada)
                    }
                    .show()
            }
        }
    }


    private fun mostrarMisCitas(usuarioId: Int) {
        val db = AppDatabase.getDatabase(this)
        val citaDao = db.citaDao()

        lifecycleScope.launch {
            citaDao.obtenerCitasPorUsuario(usuarioId).collect { lista ->
                citaAdapter = CitaAdapter(lista)
                rvMisCitas.adapter = citaAdapter
            }
        }
    }
}