package com.berna8.tfg.data.repository

import com.berna8.tfg.data.model.Reserva
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReservaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val coleccion = firestore.collection("reservas")

    suspend fun crearReserva(reserva: Reserva): Result<Unit> {
        return try {
            val docRef = coleccion.document()
            val reservaConId = reserva.copy(id = docRef.id)
            docRef.set(reservaConId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerReservasCliente(clienteUid: String): Result<List<Reserva>> {
        return try {
            val resultado = coleccion
                .whereEqualTo("clienteUid", clienteUid)
                .get()
                .await()
            val reservas = resultado.toObjects(Reserva::class.java)
            Result.success(reservas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerReservasTaller(tallerUid: String): Result<List<Reserva>> {
        return try {
            val resultado = coleccion
                .whereEqualTo("tallerUid", tallerUid)
                .get()
                .await()
            val reservas = resultado.toObjects(Reserva::class.java)
            Result.success(reservas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelarReserva(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId)
                .update("estado", "cancelada")
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmarReserva(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId)
                .update("estado", "confirmada")
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}