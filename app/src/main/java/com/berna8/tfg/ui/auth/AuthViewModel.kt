package com.berna8.tfg.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berna8.tfg.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthEstado {
    object Inactivo : AuthEstado()
    object Cargando : AuthEstado()
    data class Exito(val rol: String, val uid: String) : AuthEstado()
    data class Error(val mensaje: String) : AuthEstado()
}

class AuthViewModel : ViewModel() {

    private val repositorio = AuthRepository()

    private val _estado = MutableStateFlow<AuthEstado>(AuthEstado.Inactivo)
    val estado: StateFlow<AuthEstado> = _estado

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _estado.value = AuthEstado.Cargando
            val resultado = repositorio.login(email, password)
            _estado.value = if (resultado.isSuccess) {
                val rol = resultado.getOrDefault("cliente")
                AuthEstado.Exito(rol, repositorio.obtenerUidActual() ?: "")
            } else {
                AuthEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun registrar(nombre: String, email: String, password: String, rol: String) {
        viewModelScope.launch {
            _estado.value = AuthEstado.Cargando
            val resultado = repositorio.registrar(nombre, email, password, rol)
            _estado.value = if (resultado.isSuccess) {
                AuthEstado.Exito(rol, repositorio.obtenerUidActual() ?: "")
            } else {
                AuthEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun resetearEstado() {
        _estado.value = AuthEstado.Inactivo
    }
}