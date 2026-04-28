package com.berna8.tfg.data.repository

import com.berna8.tfg.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun registrar(nombre: String, email: String, password: String, rol: String): Result<Unit> {
        return try {
            val resultado = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = resultado.user?.uid ?: return Result.failure(Exception("Error al obtener UID"))

            val usuario = Usuario(
                uid = uid,
                nombre = nombre,
                email = email,
                rol = rol
            )

            firestore.collection("usuarios").document(uid).set(usuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Error al obtener UID"))
            val doc = firestore.collection("usuarios").document(uid).get().await()
            val rol = doc.getString("rol") ?: "cliente"
            Result.success(rol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        auth.signOut()
    }

    fun obtenerUidActual(): String? {
        return auth.currentUser?.uid
    }
}