package com.berna8.tfg.ui.taller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berna8.tfg.data.model.Taller
import com.berna8.tfg.data.repository.FavoritosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles de las operaciones de favoritos.
 */
sealed class FavoritosEstado {
    object Inactivo : FavoritosEstado()
    object Cargando : FavoritosEstado()
    object Exito : FavoritosEstado()
    data class Error(val mensaje: String) : FavoritosEstado()
}

/**
 * ViewModel encargado de gestionar los talleres favoritos del cliente.
 * Comunica la UI con FavoritosRepository.
 */
class FavoritosViewModel : ViewModel() {

    private val repositorio = FavoritosRepository()

    private val _estado = MutableStateFlow<FavoritosEstado>(FavoritosEstado.Inactivo)
    val estado: StateFlow<FavoritosEstado> = _estado

    private val _favoritos = MutableStateFlow<List<Taller>>(emptyList())
    val favoritos: StateFlow<List<Taller>> = _favoritos

    private val _esFavorito = MutableStateFlow(false)
    val esFavorito: StateFlow<Boolean> = _esFavorito

    /** Carga la lista de talleres favoritos del cliente. */
    fun cargarFavoritos(clienteUid: String) {
        viewModelScope.launch {
            _estado.value = FavoritosEstado.Cargando
            val resultado = repositorio.obtenerFavoritos(clienteUid)
            if (resultado.isSuccess) {
                _favoritos.value = resultado.getOrDefault(emptyList())
                _estado.value = FavoritosEstado.Inactivo
            } else {
                _estado.value = FavoritosEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    /** Comprueba si un taller concreto está en los favoritos del cliente. */
    fun comprobarFavorito(clienteUid: String, tallerUid: String) {
        viewModelScope.launch {
            _esFavorito.value = repositorio.esFavorito(clienteUid, tallerUid)
        }
    }

    /**
     * Alterna el estado de favorito de un taller.
     * Si ya es favorito lo elimina, si no lo añade.
     * Recarga la lista de favoritos tras el cambio.
     */
    fun toggleFavorito(clienteUid: String, tallerUid: String) {
        viewModelScope.launch {
            if (_esFavorito.value) {
                repositorio.eliminarFavorito(clienteUid, tallerUid)
                _esFavorito.value = false
            } else {
                repositorio.agregarFavorito(clienteUid, tallerUid)
                _esFavorito.value = true
            }
            cargarFavoritos(clienteUid)
        }
    }
}