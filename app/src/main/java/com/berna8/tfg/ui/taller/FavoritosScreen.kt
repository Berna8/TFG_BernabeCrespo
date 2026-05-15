package com.berna8.tfg.ui.taller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Pantalla que muestra los talleres marcados como favoritos por el cliente.
 * Permite navegar al detalle de cada taller favorito.
 */
@Composable
fun FavoritosScreen(
    clienteUid: String,
    onTallerSeleccionado: (String) -> Unit,
    viewModel: FavoritosViewModel = viewModel()
) {
    val favoritos by viewModel.favoritos.collectAsState()
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(clienteUid) {
        viewModel.cargarFavoritos(clienteUid)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Mis favoritos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        when {
            estado is FavoritosEstado.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            favoritos.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "❤️", style = MaterialTheme.typography.headlineLarge)
                        Text(text = "No tienes favoritos todavía", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Añade talleres a favoritos para acceder rápidamente",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(favoritos) { taller ->
                        TarjetaTaller(
                            taller = taller,
                            onClick = { onTallerSeleccionado(taller.uid) }
                        )
                    }
                }
            }
        }
    }
}