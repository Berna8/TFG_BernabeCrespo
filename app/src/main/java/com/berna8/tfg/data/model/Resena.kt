package com.berna8.tfg.data.model

data class Resena(
    val id: String = "",
    val clienteUid: String = "",
    val clienteNombre: String = "",
    val tallerUid: String = "",
    val puntuacion: Int = 0, // 1-5 estrellas
    val comentario: String = "",
    val fecha: String = ""
)