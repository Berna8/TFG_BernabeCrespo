package com.berna8.tfg.ui.taller

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berna8.tfg.data.model.Taller
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTalleresScreen(
    onTallerSeleccionado: (String) -> Unit,
    onVolver: () -> Unit,
    viewModel: TallerViewModel = viewModel()
) {
    val talleres by viewModel.talleres.collectAsState()
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarTodosTalleres()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Talleres disponibles") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        when {
            estado is TallerEstado.Cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            talleres.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay talleres disponibles")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(talleres) { taller ->
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

@Composable
fun TarjetaTaller(
    taller: Taller,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = taller.nombre,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = taller.direccion)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📞 ${taller.telefono}",
                style = MaterialTheme.typography.bodySmall
            )
            if (taller.servicios.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Servicios: ${taller.servicios.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}