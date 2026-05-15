package com.berna8.tfg.data.repository

import com.berna8.tfg.data.model.Reserva
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repositorio encargado de todas las operaciones relacionadas
 * con las reservas de citas en Firestore.
 */
class ReservaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val coleccion = firestore.collection("reservas")

    /**
     * Crea una nueva reserva en Firestore.
     * Genera automáticamente el ID y activa la notificación para el taller.
     */
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

    /**
     * Obtiene todas las reservas de un cliente.
     */
    suspend fun obtenerReservasCliente(clienteUid: String): Result<List<Reserva>> {
        return try {
            val resultado = coleccion.whereEqualTo("clienteUid", clienteUid).get().await()
            Result.success(resultado.toObjects(Reserva::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las reservas de un taller.
     */
    suspend fun obtenerReservasTaller(tallerUid: String): Result<List<Reserva>> {
        return try {
            val resultado = coleccion.whereEqualTo("tallerUid", tallerUid).get().await()
            Result.success(resultado.toObjects(Reserva::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Confirma una reserva y activa la notificación pendiente para el cliente.
     */
    suspend fun confirmarReserva(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId).update(
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

    /**
     * Cancela una reserva. Si esTaller es true, activa la notificación para el cliente.
     */
    suspend fun cancelarReserva(reservaId: String, esTaller: Boolean): Result<Unit> {
        return try {
            coleccion.document(reservaId).update(
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

    /**
     * Elimina una reserva de Firestore.
     */
    suspend fun eliminarReserva(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene las horas ya ocupadas para un taller, fecha y servicio concretos.
     * Fuerza la lectura desde el servidor para evitar datos desactualizados.
     */
    suspend fun obtenerHorasOcupadas(tallerUid: String, fecha: String, servicio: String): Result<List<String>> {
        return try {
            val resultado = coleccion
                .whereEqualTo("tallerUid", tallerUid)
                .whereEqualTo("fecha", fecha)
                .whereEqualTo("servicio", servicio)
                .whereNotEqualTo("estado", "cancelada")
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()
            Result.success(resultado.toObjects(Reserva::class.java).map { it.hora })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene las reservas con notificaciones pendientes para un cliente.
     */
    suspend fun obtenerNotificacionesPendientes(clienteUid: String): Result<List<Reserva>> {
        return try {
            val resultado = coleccion
                .whereEqualTo("clienteUid", clienteUid)
                .whereEqualTo("notificacionPendiente", true)
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()
            Result.success(resultado.toObjects(Reserva::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marca una notificación del cliente como leída.
     */
    suspend fun marcarNotificacionLeida(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId).update("notificacionPendiente", false).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene las reservas con notificaciones pendientes para un taller.
     */
    suspend fun obtenerNotificacionesPendientesTaller(tallerUid: String): Result<List<Reserva>> {
        return try {
            val resultado = coleccion
                .whereEqualTo("tallerUid", tallerUid)
                .whereEqualTo("notificacionPendienteTaller", true)
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()
            Result.success(resultado.toObjects(Reserva::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marca una notificación del taller como leída.
     */
    suspend fun marcarNotificacionTallerLeida(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId).update("notificacionPendienteTaller", false).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marca el coche como listo para recoger, cambia el estado a completada
     * y activa la notificación para el cliente.
     */
    suspend fun marcarCocheListo(reservaId: String): Result<Unit> {
        return try {
            coleccion.document(reservaId).update(
                mapOf(
                    "cocheListoParaRecoger" to true,
                    "estado" to "completada",
                    "notificacionPendiente" to true,
                    "mensajeNotificacion" to "Tu vehículo está listo para recoger"
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}