package com.berna8.tfg.data.model

data class Reserva(
    val id: String = "",
    val clienteUid: String = "",
    val tallerUid: String = "",
    val servicio: String = "",
    val fecha: String = "",
    val hora: String = "",
    val estado: String = "pendiente",
    val marcaCoche: String = "",
    val modeloCoche: String = "",
    val matriculaCoche: String = "",
    val notificacionPendiente: Boolean = false,
    val mensajeNotificacion: String = ""
)