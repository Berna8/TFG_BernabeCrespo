package com.berna8.tfg.ui.taller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Pantalla de búsqueda de talleres para el cliente.
 * Filtra en tiempo real por nombre, dirección o servicio.
 * Solo muestra talleres con perfil completo (validados).
 */
@Composable
fun BuscarTalleresScreen(
    clienteUid: String,
    onTallerSeleccionado: (String) -> Unit,
    viewModel: TallerViewModel = viewModel()
) {
    val talleres by viewModel.talleres.collectAsState()
    val estado by viewModel.estado.collectAsState()
    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarTodosTalleres()
    }

    // Filtra talleres por nombre, dirección o cualquier servicio
    val talleresFiltrados = talleres.filter {
        it.nombre.contains(busqueda, ignoreCase = true) ||
                it.direccion.contains(busqueda, ignoreCase = true) ||
                it.servicios.any { s -> s.contains(busqueda, ignoreCase = true) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Buscar talleres", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar por nombre, dirección o servicio") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        when {
            estado is TallerEstado.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            talleresFiltrados.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🔍", style = MaterialTheme.typography.headlineLarge)
                        Text(
                            text = if (busqueda.isBlank()) "No hay talleres disponibles"
                            else "No se encontraron resultados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(talleresFiltrados) { taller ->
                        TarjetaTaller(
                            taller = taller,
                            onClick = { onTallerSeleccionado(taller.uid) },
                            clienteUid = clienteUid
                        )
                    }
                }
            }
        }
    }
}