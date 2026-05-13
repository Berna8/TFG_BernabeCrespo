package com.berna8.tfg.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berna8.tfg.data.model.Usuario
import com.berna8.tfg.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthEstado {
    object Inactivo : AuthEstado()
    object Cargando : AuthEstado()
    object EmailNoVerificado : AuthEstado()
    data class Exito(val rol: String, val uid: String) : AuthEstado()
    data class Error(val mensaje: String) : AuthEstado()
}

class AuthViewModel : ViewModel() {

    private val repositorio = AuthRepository()

    private val _estado = MutableStateFlow<AuthEstado>(AuthEstado.Inactivo)
    val estado: StateFlow<AuthEstado> = _estado

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _perfilActualizado = MutableStateFlow(false)
    val perfilActualizado: StateFlow<Boolean> = _perfilActualizado

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

    fun reenviarEmailVerificacion() {
        viewModelScope.launch {
            repositorio.reenviarEmailVerificacion()
        }
    }

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

    fun cargarUsuario(uid: String) {
        viewModelScope.launch {
            val resultado = repositorio.obtenerUsuario(uid)
            if (resultado.isSuccess) {
                _usuario.value = resultado.getOrNull()
            }
        }
    }

    fun actualizarPerfil(uid: String, nombre: String, nombreUsuario: String) {
        viewModelScope.launch {
            val resultado = repositorio.actualizarPerfil(uid, nombre, nombreUsuario)
            _perfilActualizado.value = resultado.isSuccess
        }
    }

    fun cerrarSesion() {
        repositorio.cerrarSesion()
    }

    fun resetearEstado() {
        _estado.value = AuthEstado.Inactivo
    }

    fun resetearPerfilActualizado() {
        _perfilActualizado.value = false
    }

    fun subirFotoPerfil(context: android.content.Context, uri: android.net.Uri, uid: String) {
        viewModelScope.launch {
            val storageRepo = com.berna8.tfg.data.repository.StorageRepository(context)
            val resultado = storageRepo.subirFotoPerfil(uid, uri)
            if (resultado.isSuccess) {
                val url = resultado.getOrNull() ?: return@launch
                repositorio.actualizarFotoPerfil(uid, url)
                cargarUsuario(uid)
            }
        }
    }
}