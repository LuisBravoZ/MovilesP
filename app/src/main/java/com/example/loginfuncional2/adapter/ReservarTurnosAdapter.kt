package com.example.loginfuncional2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.loginfuncional2.R
import com.example.loginfuncional2.model.Turno

class ReservarTurnosAdapter(
    private val context: Context,
    private val turnos: List<Turno>,
    private val onReservarClick: (Turno) -> Unit,
    private val onCancelarReservarClick: (Turno) -> Unit,
    private val pacienteId: Int
) : BaseAdapter() {

    override fun getCount(): Int = turnos.size
    override fun getItem(position: Int): Any = turnos[position]
    override fun getItemId(position: Int): Long = turnos[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_reservarturnos, parent, false)

        val tvTurno = view.findViewById<TextView>(R.id.tvTurno)
        val btnReservar = view.findViewById<Button>(R.id.btnReservar)
        val btnCancelarReserva = view.findViewById<Button>(R.id.btnCancelarReserva)

        val turno = turnos[position]
        tvTurno.text = "Fecha: ${turno.fecha} - Hora: ${turno.horaInicio}"
        btnReservar.isEnabled = turno.disponible
        btnReservar.setOnClickListener { onReservarClick(turno) }

        // Solo habilita cancelar si el turno es del paciente actual
        btnCancelarReserva.isEnabled = turno.idPaciente == pacienteId
        btnCancelarReserva.setOnClickListener { onCancelarReservarClick(turno) }

        return view
    }
}