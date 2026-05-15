package com.berna8.tfg.data.repository

import com.berna8.tfg.data.model.Resena
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositorio encargado de gestionar las reseñas de los talleres en Firestore.
 */
class ResenaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val coleccion = firestore.collection("resenas")

    /**
     * Crea una nueva reseña en Firestore.
     * Genera automáticamente el ID y asigna la fecha actual.
     */
    suspend fun crearResena(resena: Resena): Result<Unit> {
        return try {
            val docRef = coleccion.document()
            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val resenaConId = resena.copy(id = docRef.id, fecha = fecha)
            docRef.set(resenaConId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las reseñas de un taller.
     * Fuerza la lectura desde el servidor para evitar datos desactualizados.
     */
    suspend fun obtenerResenasTaller(tallerUid: String): Result<List<Resena>> {
        return try {
            val resultado = coleccion
                .whereEqualTo("tallerUid", tallerUid)
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()
            Result.success(resultado.toObjects(Resena::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Comprueba si un cliente ya ha reseñado un taller concreto.
     */
    suspend fun haResenado(clienteUid: String, tallerUid: String): Boolean {
        return try {
            val resultado = coleccion
                .whereEqualTo("clienteUid", clienteUid)
                .whereEqualTo("tallerUid", tallerUid)
                .get()
                .await()
            !resultado.isEmpty
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Elimina una reseña de Firestore.
     */
    suspend fun eliminarResena(resenaId: String): Result<Unit> {
        return try {
            coleccion.document(resenaId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}