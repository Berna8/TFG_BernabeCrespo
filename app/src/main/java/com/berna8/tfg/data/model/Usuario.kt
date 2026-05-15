package com.berna8.tfg.data.model

/**
 * Modelo de datos que representa un usuario de la aplicación,
 * tanto clientes como talleres.
 */
data class Usuario(
    /** Identificador único del usuario, coincide con el UID de Firebase Auth */
    val uid: String = "",
    /** Nombre completo del usuario */
    val nombre: String = "",
    /** Nombre de usuario único para el login */
    val nombreUsuario: String = "",
    /** Correo electrónico del usuario */
    val email: String = "",
    /** Rol del usuario: cliente o taller */
    val rol: String = "",
    /** URL de la foto de perfil almacenada en Cloudinary */
    val fotoPerfil: String = "",
    /** Indica si el email ha sido verificado */
    val emailVerificado: Boolean = false
)