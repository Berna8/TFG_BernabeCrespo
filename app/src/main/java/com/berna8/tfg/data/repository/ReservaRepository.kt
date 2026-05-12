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
            val reservaConId = reserva.copy(
                id = docRef.id,
                notificacionPendienteTaller = true,
                mensajeNotificacionTaller = "Nueva cita: ${reserva.servicio} el ${reserva.fecha} a las ${reserva.hora}"
            )
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

    suspend fun confirmarReserva(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId)
                .update(
                    mapOf(
                        "estado" to "confirmada",
                        "notificacionPendiente" to true,
                        "mensajeNotificacion" to "Tu cita ha sido confirmada"
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelarReserva(reservaId: String, esTaller: Boolean): Result<Unit> {
        return try {
            coleccion.document(reservaId)
                .update(
                    mapOf(
                        "estado" to "cancelada",
                        "notificacionPendiente" to esTaller,
                        "mensajeNotificacion" to if (esTaller) "Tu cita ha sido cancelada por el taller" else ""
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarReserva(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerHorasOcupadas(tallerUid: String, fecha: String, servicio: String): Result<List<String>> {
        return try {
            val resultado = coleccion
                .whereEqualTo("tallerUid", tallerUid)
                .whereEqualTo("fecha", fecha)
                .whereEqualTo("servicio", servicio)
                .whereNotEqualTo("estado", "cancelada")
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()
            val horas = resultado.toObjects(Reserva::class.java).map { it.hora }
            Result.success(horas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerNotificacionesPendientes(clienteUid: String): Result<List<Reserva>> {
        return try {
            val resultado = coleccion
                .whereEqualTo("clienteUid", clienteUid)
                .whereEqualTo("notificacionPendiente", true)
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()
            val reservas = resultado.toObjects(Reserva::class.java)
            Result.success(reservas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun marcarNotificacionLeida(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId)
                .update("notificacionPendiente", false)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerNotificacionesPendientesTaller(tallerUid: String): Result<List<Reserva>> {
        return try {
            val resultado = coleccion
                .whereEqualTo("tallerUid", tallerUid)
                .whereEqualTo("notificacionPendienteTaller", true)
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()
            val reservas = resultado.toObjects(Reserva::class.java)
            Result.success(reservas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun marcarNotificacionTallerLeida(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId)
                .update("notificacionPendienteTaller", false)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}