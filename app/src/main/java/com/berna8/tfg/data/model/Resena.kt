package com.berna8.tfg.data.model

/**
 * Modelo de datos que representa una reseña de un cliente sobre un taller.
 */
data class Resena(
    /** Identificador único de la reseña */
    val id: String = "",
    /** UID del cliente que escribió la reseña */
    val clienteUid: String = "",
    /** Nombre del cliente que escribió la reseña */
    val clienteNombre: String = "",
    /** UID del taller al que pertenece la reseña */
    val tallerUid: String = "",
    /** Puntuación del 1 al 5 */
    val puntuacion: Int = 0,
    /** Comentario escrito por el cliente */
    val comentario: String = "",
    /** Fecha en formato dd/MM/yyyy */
    val fecha: String = ""
)