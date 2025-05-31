package com.example.loginfuncional2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.loginfuncional2.adapter.ReservarTurnosAdapter
import com.example.loginfuncional2.adapter.UsuarioAdapter
import com.example.loginfuncional2.dao.UsuarioDao
import com.example.loginfuncional2.database.AppDatabase
import com.example.loginfuncional2.model.Usuario
import com.example.loginfuncional2.utilidades.SessionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.example.loginfuncional2.model.Turno
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.map
import kotlin.jvm.java
import kotlin.let

class ReservarCitaActivity : AppCompatActivity() {

    private lateinit var spnNutricionistas: Spinner
    private lateinit var listViewTurnos: ListView
    private lateinit var adapter: ReservarTurnosAdapter// Agrega un ListView en tu XML para mostrar turnos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reservar_cita)
        spnNutricionistas = findViewById(R.id.spnNutricionistas)
        listViewTurnos = findViewById(R.id.lvListarTurnos) // Asegúrate de que exista en tu XML

        // Poblar Spinner con nombres de nutricionistas
        lifecycleScope.launch {
            val usuarioDao = AppDatabase.getDatabase(this@ReservarCitaActivity).usuarioDao()
            usuarioDao.obtenerNutricionistas().collectLatest { listaNutricionistas ->
                val nombresNutricionistas = listaNutricionistas.map { it.nombre }
                val adapter = ArrayAdapter(
                    this@ReservarCitaActivity,
                    android.R.layout.simple_spinner_item,
                    nombresNutricionistas
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                spnNutricionistas.adapter = adapter
            }
        }

        // Configurar el listener para actualizar turnos automáticamente
        spnNutricionistas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val nombreSeleccionado = parent.getItemAtPosition(position).toString()

                lifecycleScope.launch {
                    val usuarioDao = AppDatabase.getDatabase(this@ReservarCitaActivity).usuarioDao()
                    val nutricionista = usuarioDao.obtenerNutricionistaPorNombre(nombreSeleccionado)

                    nutricionista?.let {
                        actualizarListaTurnos(it.id)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        findViewById<Button>(R.id.btnAtras).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }

    private fun actualizarListaTurnos(idNutricionista: Int) {
        lifecycleScope.launch {
            val turnoDao = AppDatabase.getDatabase(this@ReservarCitaActivity).turnoDao()
            val listaTurnos = turnoDao.obtenerTurnosPorNutricionista(idNutricionista)
            mostrarTurnos(listaTurnos)
        }
    }

    private fun mostrarTurnos(listaTurnos: List<Turno>) {
        val sessionManager = SessionManager(this)
        val pacienteId = sessionManager.getUserId()
        adapter = ReservarTurnosAdapter(
            this,
            listaTurnos,
            onReservarClick = { turnoSeleccionado ->
                lifecycleScope.launch {
                    val turnoDao = AppDatabase.getDatabase(this@ReservarCitaActivity).turnoDao()
                    val turnoExistente = turnoDao.obtenerTurnoPorPaciente(pacienteId)
                    if (turnoExistente != null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@ReservarCitaActivity,
                                "Ya tienes un turno reservado.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@launch
                    }
                    val turnoActualizado = turnoSeleccionado.copy(disponible = false, idPaciente = pacienteId)
                    turnoDao.actualizarTurno(turnoActualizado)
                    actualizarListaTurnos(turnoSeleccionado.idNutricionista)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ReservarCitaActivity,
                            "Turno reservado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            onCancelarReservarClick = { turnoSeleccionado ->
                lifecycleScope.launch {
                    val turnoDao = AppDatabase.getDatabase(this@ReservarCitaActivity).turnoDao()
                    if (turnoSeleccionado.idPaciente == pacienteId) {
                        val turnoActualizado = turnoSeleccionado.copy(disponible = true, idPaciente = null)
                        turnoDao.actualizarTurno(turnoActualizado)
                        actualizarListaTurnos(turnoSeleccionado.idNutricionista)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@ReservarCitaActivity,
                                "Reserva cancelada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            pacienteId = pacienteId
        )
        listViewTurnos.adapter = adapter
    }
}