package com.berna8.tfg.data.repository

import com.berna8.tfg.data.model.Resena
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResenaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val coleccion = firestore.collection("resenas")

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

    suspend fun obtenerResenasTaller(tallerUid: String): Result<List<Resena>> {
        return try {
            val resultado = coleccion
                .whereEqualTo("tallerUid", tallerUid)
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()
            val resenas = resultado.toObjects(Resena::class.java)
            Result.success(resenas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun haResenado(clienteUid: String, tallerUid: String): Boolean {
        return try {
            val resultado = coleccion
                .whereEqualTo("clienteUid", clienteUid)
                .whereEqualTo("tallerUid", tallerUid)
                .get()
                .await()
            !resultado.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarResena(resenaId: String): Result<Unit> {
        return try {
            coleccion.document(resenaId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}