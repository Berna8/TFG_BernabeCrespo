package com.berna8.tfg.ui.taller

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.berna8.tfg.data.model.Taller

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTalleresScreen(
    onTallerSeleccionado: (String) -> Unit,
    onVolver: () -> Unit,
    clienteUid: String = "",
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
        }
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
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(talleres) { taller ->
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

@Composable
fun TarjetaTaller(
    taller: Taller,
    onClick: () -> Unit,
    clienteUid: String = "",
    viewModel: FavoritosViewModel = viewModel()
) {
    val esFavorito by viewModel.esFavorito.collectAsState()

    LaunchedEffect(taller.uid) {
        if (clienteUid.isNotBlank()) {
            viewModel.comprobarFavorito(clienteUid, taller.uid)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (taller.imagenes.isNotEmpty()) {
                AsyncImage(
                    model = taller.imagenes.first(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = taller.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (clienteUid.isNotBlank()) {
                        IconButton(
                            onClick = {
                                viewModel.toggleFavorito(clienteUid, taller.uid)
                            }
                        ) {
                            Icon(
                                imageVector = if (esFavorito)
                                    Icons.Default.Favorite
                                else
                                    Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (esFavorito)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
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
}