package com.example.loginfuncional2.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.loginfuncional2.MainActivity
import com.example.loginfuncional2.R
import com.example.loginfuncional2.utilidades.SessionManager
import com.example.loginfuncional2.adapter.UsuarioAdapter
import com.example.loginfuncional2.dao.UsuarioDao
import com.example.loginfuncional2.database.AppDatabase
import com.example.loginfuncional2.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminActivity : AppCompatActivity() {
    private lateinit var listViewUsuarios: ListView
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var adapter: UsuarioAdapter
    private var listaUsuarios = mutableListOf<Usuario>()
    private lateinit var btnAddUsuario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin)

        listViewUsuarios = findViewById(R.id.listViewUsuarios)
        usuarioDao = AppDatabase.Companion.getDatabase(this).usuarioDao()
        adapter = UsuarioAdapter(
            this, listaUsuarios,
            onEliminarClick = { usuario -> eliminarUsuario(usuario) },
            onEditarClick = { usuario -> editarUsuario(usuario) }
        )

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            SessionManager(this).clearToken()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnAddUsuario = findViewById(R.id.btnAddUsuario)
        btnAddUsuario.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }

        listViewUsuarios.adapter = adapter

        observarUsuarios()
    }

    private fun observarUsuarios() {
        lifecycleScope.launch {
            usuarioDao.obtenerUsuariosNoAdmin().collectLatest { usuarios ->
                listaUsuarios.clear()
                listaUsuarios.addAll(usuarios)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun editarUsuario(usuario: Usuario) {
        val intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("usuario_id", usuario.id)
        startActivity(intent)
    }

    private fun eliminarUsuario(usuario: Usuario) {
        lifecycleScope.launch(Dispatchers.IO) {
            usuarioDao.eliminarPorId(usuario.id)
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Usuario eliminado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}