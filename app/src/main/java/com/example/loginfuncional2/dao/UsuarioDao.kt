package com.example.loginfuncional2.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.loginfuncional2.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao{
    @Insert //insertar un registro en l tabla usuarios
    suspend fun insertar (usuario: Usuario) //se ejecuta en backgraud con corountines

    @Query("Select * from usuarios") //consulta los datos de la tabla usuarios
    fun obtenerusuarios(): Flow<List<Usuario>> // se devuelve los datos en tiempo real

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Usuario?
    // Se utiliza LIMIT 1 para asegurarse de que solo se devuelva un usuario, en caso de que haya duplicados

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarporEmail(email: String): Usuario?

    //editar un registro en la tabla usuarios
    @Query("UPDATE usuarios SET nombre = :nombre, email = :email, password = :password WHERE id = :id")
    suspend fun editarUsuario(id: Int, nombre: String, email: String, password: String)

    //eliminar un usuario
    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminarUsuario(id: Int)


}