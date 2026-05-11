package com.berna8.tfg.ui.taller

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.berna8.tfg.data.model.Resena

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTallerScreen(
    tallerUid: String,
    clienteUid: String,
    clienteNombre: String,
    onReservar: () -> Unit,
    onVolver: () -> Unit,
    tallerViewModel: TallerViewModel = viewModel(),
    resenaViewModel: ResenaViewModel = viewModel()
) {
    val taller by tallerViewModel.taller.collectAsState()
    val resenas by resenaViewModel.resenas.collectAsState()
    val puntuacionMedia by resenaViewModel.puntuacionMedia.collectAsState()
    val haResenado by resenaViewModel.haResenado.collectAsState()
    val estadoResena by resenaViewModel.estado.collectAsState()

    var mostrarFormularioResena by remember { mutableStateOf(false) }

    LaunchedEffect(tallerUid) {
        tallerViewModel.cargarTaller(tallerUid)
        resenaViewModel.cargarResenas(tallerUid)
        resenaViewModel.comprobarResena(clienteUid, tallerUid)
    }

    LaunchedEffect(estadoResena) {
        if (estadoResena is ResenaEstado.Exito) {
            mostrarFormularioResena = false
            resenaViewModel.resetearEstado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(taller?.nombre ?: "Taller") },
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
        bottomBar = {
            if (!mostrarFormularioResena) {
                Button(
                    onClick = onReservar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reservar cita")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                taller?.let { t ->
                    if (t.imagenes.isNotEmpty()) {
                        item {
                            AsyncImage(
                                model = t.imagenes.first(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = t.nombre,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "📍 ${t.direccion}")
                            Text(text = "📞 ${t.telefono}")

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { index ->
                                    Text(
                                        text = if (index < puntuacionMedia.toInt()) "★" else "☆",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (resenas.isEmpty()) "Sin reseñas"
                                    else "%.1f (${resenas.size})".format(puntuacionMedia),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    if (t.servicios.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Servicios",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                t.servicios.forEach { servicio ->
                                    Text(text = "• $servicio")
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reseñas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (!haResenado) {
                            TextButton(onClick = { mostrarFormularioResena = true }) {
                                Text("Añadir reseña")
                            }
                        }
                    }
                }

                if (resenas.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay reseñas todavía",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    items(resenas) { resena ->
                        TarjetaResena(
                            resena = resena,
                            clienteUid = clienteUid,
                            onEliminar = {
                                resenaViewModel.eliminarResena(resena.id, tallerUid)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            if (mostrarFormularioResena) {
                FormularioResena(
                    onPublicar = { puntuacionSeleccionada, comentarioEscrito ->
                        resenaViewModel.crearResena(
                            Resena(
                                clienteUid = clienteUid,
                                clienteNombre = clienteNombre,
                                tallerUid = tallerUid,
                                puntuacion = puntuacionSeleccionada,
                                comentario = comentarioEscrito
                            )
                        )
                    },
                    onCancelar = { mostrarFormularioResena = false }
                )
            }
        }
    }
}

@Composable
fun FormularioResena(
    onPublicar: (Int, String) -> Unit,
    onCancelar: () -> Unit
) {
    var puntuacion by remember { mutableStateOf(5) }
    var comentario by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nueva reseña",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Puntuación: $puntuacion",
                style = MaterialTheme.typography.titleSmall
            )

            Row {
                repeat(5) { index ->
                    Text(
                        text = if (index < puntuacion) "★" else "☆",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable { puntuacion = index + 1 }
                    )
                }
            }

            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Comentario") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 4
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = { onPublicar(puntuacion, comentario) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = comentario.isNotBlank()
                ) {
                    Text("Publicar")
                }
            }
        }
    }
}

@Composable
fun TarjetaResena(
    resena: Resena,
    clienteUid: String = "",
    onEliminar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = resena.clienteNombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = resena.fecha,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (resena.clienteUid == clienteUid) {
                        IconButton(onClick = onEliminar) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar reseña",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Row {
                repeat(5) { index ->
                    Text(
                        text = if (index < resena.puntuacion) "★" else "☆",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if (resena.comentario.isNotBlank()) {
                Text(
                    text = resena.comentario,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}