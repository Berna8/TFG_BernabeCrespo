package com.berna8.tfg.data.model

data class Taller(
    val uid: String = "",
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val servicios: List<String> = emptyList()
)