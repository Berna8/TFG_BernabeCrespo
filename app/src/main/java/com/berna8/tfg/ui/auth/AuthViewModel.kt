package com.berna8.tfg.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berna8.tfg.data.model.Usuario
import com.berna8.tfg.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles del proceso de autenticación.
 */
sealed class AuthEstado {
    object Inactivo : AuthEstado()
    object Cargando : AuthEstado()
    object EmailNoVerificado : AuthEstado()
    data class Exito(val rol: String, val uid: String) : AuthEstado()
    data class Error(val mensaje: String) : AuthEstado()
}

/**
 * ViewModel encargado de gestionar la autenticación y los datos del usuario.
 * Comunica la UI con AuthRepository.
 */
class AuthViewModel : ViewModel() {

    private val repositorio = AuthRepository()

    private val _estado = MutableStateFlow<AuthEstado>(AuthEstado.Inactivo)
    val estado: StateFlow<AuthEstado> = _estado

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _perfilActualizado = MutableStateFlow(false)
    val perfilActualizado: StateFlow<Boolean> = _perfilActualizado

    /** Inicia sesión con email o nombre de usuario y contraseña. */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _estado.value = AuthEstado.Cargando
            val resultado = repositorio.login(email, password)
            if (resultado.isSuccess) {
                val emailVerificado = repositorio.estaEmailVerificado()
                if (!emailVerificado) {
                    _estado.value = AuthEstado.EmailNoVerificado
                } else {
                    val rol = resultado.getOrDefault("cliente")
                    _estado.value = AuthEstado.Exito(rol, repositorio.obtenerUidActual() ?: "")
                }
            } else {
                _estado.value = AuthEstado.Error(
                    resultado.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    /** Registra un nuevo usuario con nombre, email, contraseña y rol. */
    fun registrar(nombre: String, email: String, password: String, rol: String) {
        viewModelScope.launch {
            _estado.value = AuthEstado.Cargando
            val resultado = repositorio.registrar(nombre, email, password, rol)
            _estado.value = if (resultado.isSuccess) {
                AuthEstado.EmailNoVerificado
            } else {
                AuthEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    /** Reenvía el email de verificación al usuario actual. */
    fun reenviarEmailVerificacion() {
        viewModelScope.launch {
            repositorio.reenviarEmailVerificacion()
        }
    }

    /** Comprueba si el usuario ha verificado su email y navega al inicio si es así. */
    fun verificarEmail() {
        viewModelScope.launch {
            _estado.value = AuthEstado.Cargando
            val verificado = repositorio.estaEmailVerificado()
            if (verificado) {
                val uid = repositorio.obtenerUidActual() ?: ""
                val doc = repositorio.obtenerUsuario(uid)
                val rol = doc.getOrNull()?.rol ?: "cliente"
                _estado.value = AuthEstado.Exito(rol, uid)
            } else {
                _estado.value = AuthEstado.EmailNoVerificado
            }
        }
    }

    /** Carga los datos del usuario desde Firestore forzando lectura desde servidor. */
    fun cargarUsuario(uid: String) {
        viewModelScope.launch {
            val resultado = repositorio.obtenerUsuario(uid, forceServer = true)
            if (resultado.isSuccess) {
                _usuario.value = resultado.getOrNull()
            }
        }
    }

    /** Actualiza el nombre y nombre de usuario en Firestore. */
    fun actualizarPerfil(uid: String, nombre: String, nombreUsuario: String) {
        viewModelScope.launch {
            val resultado = repositorio.actualizarPerfil(uid, nombre, nombreUsuario)
            _perfilActualizado.value = resultado.isSuccess
        }
    }

    /** Cierra la sesión del usuario actual. */
    fun cerrarSesion() {
        repositorio.cerrarSesion()
    }

    /** Resetea el estado de autenticación a Inactivo. */
    fun resetearEstado() {
        _estado.value = AuthEstado.Inactivo
    }

    /** Resetea el flag de perfil actualizado. */
    fun resetearPerfilActualizado() {
        _perfilActualizado.value = false
    }

    /**
     * Sube una nueva foto de perfil a Cloudinary y actualiza la URL en Firestore.
     * Añade un timestamp a la URL para evitar problemas de caché.
     */
    fun subirFotoPerfil(context: android.content.Context, uri: android.net.Uri, uid: String) {
        viewModelScope.launch {
            val storageRepo = com.berna8.tfg.data.repository.StorageRepository(context)
            val resultado = storageRepo.subirFotoPerfil(uid, uri)
            if (resultado.isSuccess) {
                val url = resultado.getOrNull() ?: return@launch
                val urlConTimestamp = "$url?t=${System.currentTimeMillis()}"
                repositorio.actualizarFotoPerfil(uid, urlConTimestamp)
                cargarUsuario(uid)
            } else {
                android.util.Log.e("FotoPerfil", "Error: ${resultado.exceptionOrNull()?.message}")
            }
        }
    }
}