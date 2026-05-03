package com.berna8.tfg.data.repository

import com.berna8.tfg.data.model.Taller
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoritosRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun obtenerFavoritos(clienteUid: String): Result<List<Taller>> {
        return try {
            val doc = firestore.collection("usuarios")
                .document(clienteUid)
                .get()
                .await()
            val tallerIds = doc.get("favoritos") as? List<String> ?: emptyList()
            val talleres = mutableListOf<Taller>()
            tallerIds.forEach { tallerId ->
                val tallerDoc = firestore.collection("talleres")
                    .document(tallerId)
                    .get()
                    .await()
                tallerDoc.toObject(Taller::class.java)?.let { talleres.add(it) }
            }
            Result.success(talleres)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun agregarFavorito(clienteUid: String, tallerUid: String): Result<Unit> {
        return try {
            val doc = firestore.collection("usuarios").document(clienteUid).get().await()
            val favoritos = (doc.get("favoritos") as? List<String> ?: emptyList()).toMutableList()
            if (!favoritos.contains(tallerUid)) {
                favoritos.add(tallerUid)
                firestore.collection("usuarios").document(clienteUid)
                    .update("favoritos", favoritos).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarFavorito(clienteUid: String, tallerUid: String): Result<Unit> {
        return try {
            val doc = firestore.collection("usuarios").document(clienteUid).get().await()
            val favoritos = (doc.get("favoritos") as? List<String> ?: emptyList()).toMutableList()
            favoritos.remove(tallerUid)
            firestore.collection("usuarios").document(clienteUid)
                .update("favoritos", favoritos).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun esFavorito(clienteUid: String, tallerUid: String): Boolean {
        return try {
            val doc = firestore.collection("usuarios").document(clienteUid).get().await()
            val favoritos = doc.get("favoritos") as? List<String> ?: emptyList()
            favoritos.contains(tallerUid)
        } catch (e: Exception) {
            false
        }
    }
}