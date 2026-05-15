package com.berna8.tfg.data.repository

import com.berna8.tfg.data.model.Taller
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repositorio encargado de todas las operaciones relacionadas
 * con los talleres en Firestore.
 */
class TallerRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val coleccion = firestore.collection("talleres")

    /**
     * Crea un nuevo taller en Firestore usando su UID como ID del documento.
     */
    suspend fun crearTaller(taller: Taller): Result<Unit> {
        return try {
            coleccion.document(taller.uid).set(taller).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los datos de un taller por su UID.
     */
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

    /**
     * Obtiene la lista de todos los talleres validados.
     * Solo devuelve talleres con perfil completo (nombre, dirección, teléfono y al menos un servicio).
     * Fuerza la lectura desde el servidor para evitar datos desactualizados.
     */
    suspend fun obtenerTodosTalleres(): Result<List<Taller>> {
        return try {
            val resultado = coleccion
                .get(com.google.firebase.firestore.Source.SERVER)
                .await()
            val talleres = resultado.toObjects(Taller::class.java)
                .filter { taller ->
                    taller.nombre.isNotBlank() &&
                            taller.direccion.isNotBlank() &&
                            taller.telefono.isNotBlank() &&
                            taller.servicios.isNotEmpty()
                }
            Result.success(talleres)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza los datos de un taller existente en Firestore.
     */
    suspend fun actualizarTaller(taller: Taller): Result<Unit> {
        return try {
            coleccion.document(taller.uid).set(taller).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}