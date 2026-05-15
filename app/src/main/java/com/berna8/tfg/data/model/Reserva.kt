package com.berna8.tfg.data.model

/**
 * Modelo de datos que representa una reserva de cita en un taller.
 */
data class Reserva(
    /** Identificador único de la reserva */
    val id: String = "",
    /** UID del cliente que realizó la reserva */
    val clienteUid: String = "",
    /** UID del taller donde se realiza la reserva */
    val tallerUid: String = "",
    /** Servicio solicitado */
    val servicio: String = "",
    /** Fecha de la cita en formato dd/MM/yyyy */
    val fecha: String = "",
    /** Hora de la cita en formato HH:mm */
    val hora: String = "",
    /** Estado de la reserva: pendiente, confirmada, cancelada o completada */
    val estado: String = "pendiente",
    /** Marca del vehículo */
    val marcaCoche: String = "",
    /** Modelo del vehículo */
    val modeloCoche: String = "",
    /** Matrícula del vehículo */
    val matriculaCoche: String = "",
    /** Indica si hay una notificación pendiente para el cliente */
    val notificacionPendiente: Boolean = false,
    /** Mensaje de la notificación para el cliente */
    val mensajeNotificacion: String = "",
    /** Indica si hay una notificación pendiente para el taller */
    val notificacionPendienteTaller: Boolean = false,
    /** Mensaje de la notificación para el taller */
    val mensajeNotificacionTaller: String = "",
    /** Indica si el taller ha notificado que el coche está listo para recoger */
    val cocheListoParaRecoger: Boolean = false
)