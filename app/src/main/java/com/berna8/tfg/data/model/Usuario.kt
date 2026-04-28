package com.berna8.tfg.data.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = "" // "cliente" o "taller"
)