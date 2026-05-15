package com.berna8.tfg.data.model

/**
 * Modelo de datos que representa un taller mecánico en la aplicación.
 */
data class Taller(
    /** Identificador único del taller, coincide con el UID de Firebase Auth */
    val uid: String = "",
    /** Nombre del taller */
    val nombre: String = "",
    /** Dirección física del taller */
    val direccion: String = "",
    /** Teléfono de contacto */
    val telefono: String = "",
    /** Lista de servicios ofrecidos por el taller */
    val servicios: List<String> = emptyList(),
    /** Lista de horarios disponibles en formato HH:mm */
    val horariosDisponibles: List<String> = emptyList(),
    /** Lista de URLs de imágenes del taller almacenadas en Cloudinary */
    val imagenes: List<String> = emptyList()
)