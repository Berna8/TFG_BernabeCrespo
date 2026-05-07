package com.berna8.tfg.ui.taller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berna8.tfg.data.model.Taller
import com.berna8.tfg.data.repository.TallerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TallerEstado {
    object Inactivo : TallerEstado()
    object Cargando : TallerEstado()
    object Exito : TallerEstado()
    data class Error(val mensaje: String) : TallerEstado()
}

class TallerViewModel : ViewModel() {

    private val repositorio = TallerRepository()

    private val _estado = MutableStateFlow<TallerEstado>(TallerEstado.Inactivo)
    val estado: StateFlow<TallerEstado> = _estado

    private val _taller = MutableStateFlow<Taller?>(null)
    val taller: StateFlow<Taller?> = _taller

    private val _talleres = MutableStateFlow<List<Taller>>(emptyList())
    val talleres: StateFlow<List<Taller>> = _talleres

    private val _imagenes = MutableStateFlow<List<String>>(emptyList())
    val imagenes: StateFlow<List<String>> = _imagenes

    fun cargarTaller(uid: String) {
        viewModelScope.launch {
            _estado.value = TallerEstado.Cargando
            val resultado = repositorio.obtenerTaller(uid)
            if (resultado.isSuccess) {
                _taller.value = resultado.getOrNull()
                _estado.value = TallerEstado.Inactivo
            } else {
                _estado.value = TallerEstado.Inactivo
            }
        }
    }

    fun cargarTodosTalleres() {
        viewModelScope.launch {
            _estado.value = TallerEstado.Cargando
            val resultado = repositorio.obtenerTodosTalleres()
            if (resultado.isSuccess) {
                _talleres.value = resultado.getOrDefault(emptyList())
                _estado.value = TallerEstado.Inactivo
            } else {
                _estado.value = TallerEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun guardarTaller(taller: Taller) {
        viewModelScope.launch {
            _estado.value = TallerEstado.Cargando
            val resultado = if (_taller.value == null) {
                repositorio.crearTaller(taller)
            } else {
                repositorio.actualizarTaller(taller)
            }
            _estado.value = if (resultado.isSuccess) {
                _taller.value = taller
                TallerEstado.Exito
            } else {
                TallerEstado.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun resetearEstado() {
        _estado.value = TallerEstado.Inactivo
    }

    fun subirImagen(context: android.content.Context, uri: android.net.Uri, tallerUid: String) {
        viewModelScope.launch {
            _estado.value = TallerEstado.Cargando
            val storageRepo = com.berna8.tfg.data.repository.StorageRepository(context)
            val resultado = storageRepo.subirImagenTaller(uri)
            if (resultado.isSuccess) {
                val url = resultado.getOrNull() ?: return@launch
                val nuevasImagenes = (_taller.value?.imagenes ?: emptyList()) + url
                val tallerActualizado = _taller.value?.copy(imagenes = nuevasImagenes) ?: return@launch
                repositorio.actualizarTaller(tallerActualizado)
                _taller.value = tallerActualizado
                _estado.value = TallerEstado.Exito
            } else {
                _estado.value = TallerEstado.Error(
                    resultado.exceptionOrNull()?.message ?: "Error al subir imagen"
                )
            }
        }
    }

    fun eliminarImagen(url: String, tallerUid: String) {
        viewModelScope.launch {
            val nuevasImagenes = (_taller.value?.imagenes ?: emptyList()) - url
            val tallerActualizado = _taller.value?.copy(imagenes = nuevasImagenes) ?: return@launch
            repositorio.actualizarTaller(tallerActualizado)
            _taller.value = tallerActualizado
        }
    }
}