package com.berna8.tfg.ui.reserva

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berna8.tfg.data.model.Reserva
import com.berna8.tfg.data.repository.ReservaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles de las operaciones de reserva.
 */
sealed class ReservaEstado {
    object Inactivo : ReservaEstado()
    object Cargando : ReservaEstado()
    object Exito : ReservaEstado()
    data class Error(val mensaje: String) : ReservaEstado()
}

/**
 * ViewModel encargado de gestionar las reservas de citas.
 * Comunica la UI con ReservaRepository y gestiona las notificaciones locales.
 */
class ReservaViewModel : ViewModel() {

    private val repositorio = ReservaRepository()

    private val _estado = MutableStateFlow<ReservaEstado>(ReservaEstado.Inactivo)
    val estado: StateFlow<ReservaEstado> = _estado

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas

    private val _horasOcupadas = MutableStateFlow<List<String>>(emptyList())
    val horasOcupadas: StateFlow<List<String>> = _horasOcupadas

    /** Crea una nueva reserva en Firestore. */
    fun crearReserva(reserva: Reserva) {
        viewModelScope.launch {
            _estado.value = ReservaEstado.Cargando
            val resultado = repositorio.crearReserva(reserva)
            _estado.value = if (resultado.isSuccess) ReservaEstado.Exito
            else ReservaEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
        }
    }

    /** Carga todas las reservas de un cliente. */
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

    /** Carga todas las reservas de un taller. */
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

    /**
     * Cancela una reserva. Si esTaller es true recarga las reservas del taller,
     * si es false recarga las del cliente.
     */
    fun cancelarReserva(reservaId: String, uid: String, esTaller: Boolean) {
        viewModelScope.launch {
            val resultado = repositorio.cancelarReserva(reservaId, esTaller)
            if (resultado.isSuccess) {
                if (esTaller) cargarReservasTaller(uid) else cargarReservasCliente(uid)
            } else {
                _estado.value = ReservaEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    /** Confirma una reserva y recarga las reservas del taller. */
    fun confirmarReserva(reservaId: String, tallerUid: String) {
        viewModelScope.launch {
            val resultado = repositorio.confirmarReserva(reservaId)
            if (resultado.isSuccess) cargarReservasTaller(tallerUid)
            else _estado.value = ReservaEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
        }
    }

    /** Resetea el estado a Inactivo. */
    fun resetearEstado() {
        _estado.value = ReservaEstado.Inactivo
    }

    /** Elimina una reserva y recarga la lista correspondiente. */
    fun eliminarReserva(reservaId: String, uid: String, esTaller: Boolean) {
        viewModelScope.launch {
            val resultado = repositorio.eliminarReserva(reservaId)
            if (resultado.isSuccess) {
                if (esTaller) cargarReservasTaller(uid) else cargarReservasCliente(uid)
            } else {
                _estado.value = ReservaEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    /** Carga las horas ocupadas para un taller, fecha y servicio concretos. */
    fun cargarHorasOcupadas(tallerUid: String, fecha: String, servicio: String) {
        viewModelScope.launch {
            val resultado = repositorio.obtenerHorasOcupadas(tallerUid, fecha, servicio)
            if (resultado.isSuccess) {
                _horasOcupadas.value = resultado.getOrDefault(emptyList())
            }
        }
    }

    /**
     * Comprueba las notificaciones pendientes del cliente y las muestra como notificaciones locales.
     * Marca cada notificación como leída tras mostrarla.
     */
    fun comprobarNotificaciones(clienteUid: String, context: android.content.Context) {
        viewModelScope.launch {
            val resultado = repositorio.obtenerNotificacionesPendientes(clienteUid)
            if (resultado.isSuccess) {
                resultado.getOrDefault(emptyList()).forEach { reserva ->
                    com.berna8.tfg.utils.NotificacionHelper.mostrarNotificacion(
                        context = context,
                        titulo = "AutoCita",
                        mensaje = reserva.mensajeNotificacion
                    )
                    repositorio.marcarNotificacionLeida(reserva.id)
                }
            }
        }
    }

    /**
     * Comprueba las notificaciones pendientes del taller y las muestra como notificaciones locales.
     * Marca cada notificación como leída tras mostrarla.
     */
    fun comprobarNotificacionesTaller(tallerUid: String, context: android.content.Context) {
        viewModelScope.launch {
            val resultado = repositorio.obtenerNotificacionesPendientesTaller(tallerUid)
            if (resultado.isSuccess) {
                resultado.getOrDefault(emptyList()).forEach { reserva ->
                    com.berna8.tfg.utils.NotificacionHelper.mostrarNotificacion(
                        context = context,
                        titulo = "Nueva cita",
                        mensaje = reserva.mensajeNotificacionTaller
                    )
                    repositorio.marcarNotificacionTallerLeida(reserva.id)
                }
            }
        }
    }

    /** Marca el coche como listo y recarga las reservas del taller. */
    fun marcarCocheListo(reservaId: String, tallerUid: String) {
        viewModelScope.launch {
            val resultado = repositorio.marcarCocheListo(reservaId)
            if (resultado.isSuccess) cargarReservasTaller(tallerUid)
            else _estado.value = ReservaEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
        }
    }
}