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

            // Enviar email de verificación
            auth.currentUser?.sendEmailVerification()?.await()

            val usuario = Usuario(
                uid = uid,
                nombre = nombre,
                nombreUsuario = nombre.lowercase().replace(" ", "_"),
                email = email,
                rol = rol,
                emailVerificado = false
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

    suspend fun obtenerUsuario(uid: String): Result<Usuario> {
        return try {
            val doc = firestore.collection("usuarios").document(uid).get().await()
            val usuario = doc.toObject(Usuario::class.java)
                ?: return Result.failure(Exception("Usuario no encontrado"))
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarPerfil(uid: String, nombre: String, nombreUsuario: String): Result<Unit> {
        return try {
            firestore.collection("usuarios").document(uid)
                .update(
                    mapOf(
                        "nombre" to nombre,
                        "nombreUsuario" to nombreUsuario
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun estaEmailVerificado(): Boolean {
        auth.currentUser?.reload()?.await()
        return auth.currentUser?.isEmailVerified ?: false
    }

    suspend fun reenviarEmailVerificacion(): Result<Unit> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Result.success(Unit)
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

    suspend fun obtenerRol(uid: String): String {
        return try {
            val doc = firestore.collection("usuarios").document(uid).get().await()
            doc.getString("rol") ?: "cliente"
        } catch (e: Exception) {
            "cliente"
        }
    }

    suspend fun actualizarFotoPerfil(uid: String, url: String): Result<Unit> {
        return try {
            firestore.collection("usuarios").document(uid)
                .update("fotoPerfil", url)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}