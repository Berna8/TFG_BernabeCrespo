package com.berna8.tfg.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berna8.tfg.data.model.Reserva
import com.berna8.tfg.ui.reserva.ReservaEstado
import com.berna8.tfg.ui.reserva.ReservaViewModel

@Composable
fun HistorialTallerScreen(
    tallerUid: String,
    viewModel: ReservaViewModel = viewModel()
) {
    val reservas by viewModel.reservas.collectAsState()
    val estado by viewModel.estado.collectAsState()
    var busqueda by remember { mutableStateOf("") }

    val reservasFiltradas = if (busqueda.isBlank()) reservas
    else reservas.filter { it.matriculaCoche.contains(busqueda, ignoreCase = true) }

    val citasCanceladas = reservasFiltradas.filter { it.estado == "cancelada" }
    val citasCompletadas = reservasFiltradas.filter { it.estado == "completada" }
    val citasConfirmadas = reservasFiltradas.filter { it.estado == "confirmada" }

    LaunchedEffect(tallerUid) {
        viewModel.cargarReservasTaller(tallerUid)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Historial",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar por matrícula") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            estado is ReservaEstado.Cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            citasCompletadas.isEmpty() && citasCanceladas.isEmpty() && citasConfirmadas.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📋",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = if (busqueda.isBlank()) "No hay historial todavía"
                            else "No se encontraron resultados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    if (citasConfirmadas.isNotEmpty()) {
                        item {
                            Text(
                                text = "Confirmadas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(citasConfirmadas) { reserva ->
                            TarjetaHistorialTaller(
                                reserva = reserva,
                                onCocheListo = {
                                    viewModel.marcarCocheListo(reserva.id, tallerUid)
                                }
                            )
                        }
                    }

                    if (citasCompletadas.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Completadas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(citasCompletadas) { reserva ->
                            TarjetaHistorialTaller(reserva = reserva)
                        }
                    }

                    if (citasCanceladas.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Canceladas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        items(citasCanceladas) { reserva ->
                            TarjetaHistorialTaller(reserva = reserva)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaHistorialTaller(
    reserva: Reserva,
    onCocheListo: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = reserva.servicio,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                EstadoChip(estado = reserva.estado)
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${reserva.fecha} a las ${reserva.hora}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (reserva.marcaCoche.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🚗 ${reserva.marcaCoche} ${reserva.modeloCoche} - ${reserva.matriculaCoche}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (reserva.estado == "confirmada" && onCocheListo != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCocheListo,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🔔 Notificar coche listo")
                }
            }
        }
    }
}
@Composable
fun EstadoChip(estado: String) {
    val (color, emoji) = when (estado) {
        "confirmada" -> Pair(MaterialTheme.colorScheme.primary, "✅")
        "completada" -> Pair(MaterialTheme.colorScheme.primary, "🏁")
        "cancelada" -> Pair(MaterialTheme.colorScheme.error, "❌")
        else -> Pair(MaterialTheme.colorScheme.secondary, "⏳")
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = "$emoji ${estado.replaceFirstChar { it.uppercase() }}",
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}