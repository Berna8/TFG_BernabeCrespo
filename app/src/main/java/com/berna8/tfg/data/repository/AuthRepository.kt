package com.berna8.tfg.data.repository

import com.berna8.tfg.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repositorio encargado de todas las operaciones de autenticación
 * y gestión de usuarios con Firebase Auth y Firestore.
 */
class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Registra un nuevo usuario en Firebase Auth y crea su documento en Firestore.
     * Envía un email de verificación automáticamente tras el registro.
     */
    suspend fun registrar(nombre: String, email: String, password: String, rol: String): Result<Unit> {
        return try {
            val resultado = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = resultado.user?.uid ?: return Result.failure(Exception("Error al obtener UID"))
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

    /**
     * Inicia sesión con email o nombre de usuario y contraseña.
     * Si el campo no contiene @, busca el email asociado al nombre de usuario.
     * Devuelve el rol del usuario (cliente o taller).
     */
    suspend fun login(emailOUsuario: String, password: String): Result<String> {
        return try {
            val email = if (emailOUsuario.contains("@")) {
                emailOUsuario
            } else {
                val resultado = obtenerEmailPorNombreUsuario(emailOUsuario)
                if (resultado.isFailure) return Result.failure(
                    resultado.exceptionOrNull() ?: Exception("Usuario no encontrado")
                )
                resultado.getOrNull() ?: return Result.failure(Exception("Email no encontrado"))
            }
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Error al obtener UID"))
            val doc = firestore.collection("usuarios").document(uid).get().await()
            val rol = doc.getString("rol") ?: "cliente"
            Result.success(rol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los datos de un usuario desde Firestore.
     * Si forceServer es true, fuerza la lectura desde el servidor ignorando la caché.
     */
    suspend fun obtenerUsuario(uid: String, forceServer: Boolean = false): Result<Usuario> {
        return try {
            val doc = if (forceServer) {
                firestore.collection("usuarios").document(uid)
                    .get(com.google.firebase.firestore.Source.SERVER)
                    .await()
            } else {
                firestore.collection("usuarios").document(uid).get().await()
            }
            val usuario = doc.toObject(Usuario::class.java)
                ?: return Result.failure(Exception("Usuario no encontrado"))
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el nombre y nombre de usuario en Firestore.
     */
    suspend fun actualizarPerfil(uid: String, nombre: String, nombreUsuario: String): Result<Unit> {
        return try {
            firestore.collection("usuarios").document(uid)
                .update(mapOf("nombre" to nombre, "nombreUsuario" to nombreUsuario))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Recarga el usuario actual y comprueba si ha verificado su email.
     */
    suspend fun estaEmailVerificado(): Boolean {
        auth.currentUser?.reload()?.await()
        return auth.currentUser?.isEmailVerified ?: false
    }

    /**
     * Reenvía el email de verificación al usuario actual.
     */
    suspend fun reenviarEmailVerificacion(): Result<Unit> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Cierra la sesión del usuario actual. */
    fun cerrarSesion() {
        auth.signOut()
    }

    /** Devuelve el UID del usuario actualmente autenticado. */
    fun obtenerUidActual(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Obtiene el rol del usuario desde Firestore.
     * Devuelve "cliente" por defecto si hay algún error.
     */
    suspend fun obtenerRol(uid: String): String {
        return try {
            val doc = firestore.collection("usuarios").document(uid).get().await()
            doc.getString("rol") ?: "cliente"
        } catch (_: Exception) {
            "cliente"
        }
    }

    /**
     * Actualiza la URL de la foto de perfil del usuario en Firestore.
     */
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

    /**
     * Busca el email asociado a un nombre de usuario en Firestore.
     * Se usa para permitir el login con nombre de usuario.
     */
    suspend fun obtenerEmailPorNombreUsuario(nombreUsuario: String): Result<String> {
        return try {
            val resultado = firestore.collection("usuarios")
                .whereEqualTo("nombreUsuario", nombreUsuario)
                .get()
                .await()
            if (resultado.isEmpty) {
                Result.failure(Exception("Usuario no encontrado"))
            } else {
                val email = resultado.documents.first().getString("email") ?: ""
                Result.success(email)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}