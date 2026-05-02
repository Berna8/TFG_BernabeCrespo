package com.berna8.tfg.data.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val nombreUsuario: String = "",
    val email: String = "",
    val rol: String = "",
    val fotoPerfil: String = "",
    val emailVerificado: Boolean = false
)