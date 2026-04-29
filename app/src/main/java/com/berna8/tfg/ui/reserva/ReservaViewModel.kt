package com.berna8.tfg.ui.reserva

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berna8.tfg.data.model.Reserva
import com.berna8.tfg.data.repository.ReservaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReservaEstado {
    object Inactivo : ReservaEstado()
    object Cargando : ReservaEstado()
    object Exito : ReservaEstado()
    data class Error(val mensaje: String) : ReservaEstado()
}

class ReservaViewModel : ViewModel() {

    private val repositorio = ReservaRepository()

    private val _estado = MutableStateFlow<ReservaEstado>(ReservaEstado.Inactivo)
    val estado: StateFlow<ReservaEstado> = _estado

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas

    fun crearReserva(reserva: Reserva) {
        viewModelScope.launch {
            _estado.value = ReservaEstado.Cargando
            val resultado = repositorio.crearReserva(reserva)
            _estado.value = if (resultado.isSuccess) {
                ReservaEstado.Exito
            } else {
                ReservaEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun cargarReservasCliente(clienteUid: String) {
        viewModelScope.launch {
            _estado.value = ReservaEstado.Cargando
            val resultado = repositorio.obtenerReservasCliente(clienteUid)
            if (resultado.isSuccess) {
                _reservas.value = resultado.getOrDefault(emptyList())
                _estado.value = ReservaEstado.Inactivo
            } else {
                _estado.value = ReservaEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun cargarReservasTaller(tallerUid: String) {
        viewModelScope.launch {
            _estado.value = ReservaEstado.Cargando
            val resultado = repositorio.obtenerReservasTaller(tallerUid)
            if (resultado.isSuccess) {
                _reservas.value = resultado.getOrDefault(emptyList())
                _estado.value = ReservaEstado.Inactivo
            } else {
                _estado.value = ReservaEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun cancelarReserva(reservaId: String, uid: String, esTaller: Boolean) {
        viewModelScope.launch {
            val resultado = repositorio.cancelarReserva(reservaId)
            if (resultado.isSuccess) {
                if (esTaller) cargarReservasTaller(uid)
                else cargarReservasCliente(uid)
            } else {
                _estado.value = ReservaEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun confirmarReserva(reservaId: String, tallerUid: String) {
        viewModelScope.launch {
            val resultado = repositorio.confirmarReserva(reservaId)
            if (resultado.isSuccess) {
                cargarReservasTaller(tallerUid)
            } else {
                _estado.value = ReservaEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun resetearEstado() {
        _estado.value = ReservaEstado.Inactivo
    }
}