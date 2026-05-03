package com.berna8.tfg.data.repository

import com.berna8.tfg.data.model.Taller
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TallerRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val coleccion = firestore.collection("talleres")

    suspend fun crearTaller(taller: Taller): Result<Unit> {
        return try {
            coleccion.document(taller.uid).set(taller).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerTaller(uid: String): Result<Taller> {
        return try {
            val doc = coleccion.document(uid).get().await()
            val taller = doc.toObject(Taller::class.java)
                ?: return Result.failure(Exception("Taller no encontrado"))
            Result.success(taller)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerTodosTalleres(): Result<List<Taller>> {
        return try {
            val resultado = coleccion
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()
            val talleres = resultado.toObjects(Taller::class.java)
            Result.success(talleres)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarTaller(taller: Taller): Result<Unit> {
        return try {
            coleccion.document(taller.uid).set(taller).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}