package com.berna8.tfg.ui.taller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berna8.tfg.data.model.Resena
import com.berna8.tfg.data.repository.ResenaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ResenaEstado {
    object Inactivo : ResenaEstado()
    object Cargando : ResenaEstado()
    object Exito : ResenaEstado()
    data class Error(val mensaje: String) : ResenaEstado()
}

class ResenaViewModel : ViewModel() {

    private val repositorio = ResenaRepository()

    private val _estado = MutableStateFlow<ResenaEstado>(ResenaEstado.Inactivo)
    val estado: StateFlow<ResenaEstado> = _estado

    private val _resenas = MutableStateFlow<List<Resena>>(emptyList())
    val resenas: StateFlow<List<Resena>> = _resenas

    private val _haResenado = MutableStateFlow(false)
    val haResenado: StateFlow<Boolean> = _haResenado

    private val _puntuacionMedia = MutableStateFlow(0f)
    val puntuacionMedia: StateFlow<Float> = _puntuacionMedia

    fun cargarResenas(tallerUid: String) {
        viewModelScope.launch {
            _estado.value = ResenaEstado.Cargando
            val resultado = repositorio.obtenerResenasTaller(tallerUid)
            if (resultado.isSuccess) {
                val lista = resultado.getOrDefault(emptyList())
                _resenas.value = lista
                _puntuacionMedia.value = if (lista.isEmpty()) 0f
                else lista.map { it.puntuacion }.average().toFloat()
                _estado.value = ResenaEstado.Inactivo
            } else {
                _estado.value = ResenaEstado.Error(
                    resultado.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    fun comprobarResena(clienteUid: String, tallerUid: String) {
        viewModelScope.launch {
            _haResenado.value = repositorio.haResenado(clienteUid, tallerUid)
        }
    }

    fun crearResena(resena: Resena) {
        viewModelScope.launch {
            _estado.value = ResenaEstado.Cargando
            val resultado = repositorio.crearResena(resena)
            _estado.value = if (resultado.isSuccess) {
                _haResenado.value = true
                cargarResenas(resena.tallerUid)
                ResenaEstado.Exito
            } else {
                ResenaEstado.Error(
                    resultado.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    fun resetearEstado() {
        _estado.value = ResenaEstado.Inactivo
    }

    fun eliminarResena(resenaId: String, tallerUid: String) {
        viewModelScope.launch {
            val resultado = repositorio.eliminarResena(resenaId)
            if (resultado.isSuccess) {
                _haResenado.value = false
                cargarResenas(tallerUid)
            }
        }
    }
}