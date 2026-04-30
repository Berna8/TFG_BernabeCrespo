package com.berna8.tfg.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berna8.tfg.data.model.Reserva
import com.berna8.tfg.ui.reserva.ReservaEstado
import com.berna8.tfg.ui.reserva.ReservaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTallerScreen(
    tallerUid: String,
    onCerrarSesion: () -> Unit,
    onEditarPerfil: () -> Unit,
    viewModel: ReservaViewModel = viewModel()
) {
    val reservas by viewModel.reservas.collectAsState()
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(tallerUid) {
        viewModel.cargarReservasTaller(tallerUid)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Citas del taller") },
                actions = {
                    TextButton(onClick = onEditarPerfil) {
                        Text("Mi perfil")
                    }
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir")
                    }
                }
            )
        },
    ) { paddingValues ->
        when {
            estado is ReservaEstado.Cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            reservas.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay citas programadas")
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
                    items(reservas) { reserva ->
                        TarjetaReservaTaller(
                            reserva = reserva,
                            onConfirmar = {
                                viewModel.confirmarReserva(reserva.id, tallerUid)
                            },
                            onCancelar = {
                                viewModel.cancelarReserva(reserva.id, tallerUid, true)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaReservaTaller(
    reserva: Reserva,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = reserva.servicio,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Fecha: ${reserva.fecha} a las ${reserva.hora}")
            Spacer(modifier = Modifier.height(4.dp))
            EstadoChip(estado = reserva.estado)
            if (reserva.estado == "pendiente") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = onConfirmar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}