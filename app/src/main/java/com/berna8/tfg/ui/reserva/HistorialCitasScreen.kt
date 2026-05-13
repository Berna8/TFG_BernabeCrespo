package com.berna8.tfg.ui.reserva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berna8.tfg.data.model.Reserva
import com.berna8.tfg.ui.home.EstadoChip

@Composable
fun HistorialCitasScreen(
    clienteUid: String,
    onVolver: () -> Unit,
    viewModel: ReservaViewModel = viewModel()
) {
    val reservas by viewModel.reservas.collectAsState()
    val estado by viewModel.estado.collectAsState()

    val citasPasadas = reservas.filter { it.estado == "cancelada" || it.estado == "completada" }
    val citasFuturas = reservas.filter { it.estado == "pendiente" || it.estado == "confirmada" }

    LaunchedEffect(clienteUid) {
        viewModel.cargarReservasCliente(clienteUid)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Mis citas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        when {
            estado is ReservaEstado.Cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            reservas.isEmpty() -> {
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
                            text = "No tienes citas",
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
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    if (citasFuturas.isNotEmpty()) {
                        item {
                            Text(
                                text = "Próximas citas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(citasFuturas) { reserva ->
                            TarjetaHistorial(
                                reserva = reserva,
                                onCancelar = {
                                    viewModel.cancelarReserva(reserva.id, clienteUid, false)
                                }
                            )
                        }
                    }
                    if (citasPasadas.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Citas anteriores",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        items(citasPasadas) { reserva ->
                            TarjetaHistorial(
                                reserva = reserva,
                                onEliminar = {
                                    viewModel.eliminarReserva(reserva.id, clienteUid, false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaHistorial(
    reserva: Reserva,
    onCancelar: (() -> Unit)? = null,
    onEliminar: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = reserva.servicio,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                EstadoChip(estado = reserva.estado)
            }
            HorizontalDivider()
            Text(
                text = "📅 ${reserva.fecha} a las ${reserva.hora}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (reserva.marcaCoche.isNotBlank()) {
                Text(
                    text = "🚗 ${reserva.marcaCoche} ${reserva.modeloCoche} - ${reserva.matriculaCoche}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (onCancelar != null) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancelar cita")
                }
            }

            if (onEliminar != null) {
                OutlinedButton(
                    onClick = onEliminar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}