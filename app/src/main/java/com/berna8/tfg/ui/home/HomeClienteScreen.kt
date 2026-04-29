package com.berna8.tfg.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.berna8.tfg.data.model.Reserva

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeClienteScreen(
    onCerrarSesion: () -> Unit,
    onNuevaReserva: () -> Unit
) {
    // Lista de reservas de prueba por ahora
    val reservas = remember {
        listOf(
            Reserva(
                id = "1",
                servicio = "Cambio de aceite",
                fecha = "05/05/2025",
                hora = "10:00",
                estado = "confirmada"
            ),
            Reserva(
                id = "2",
                servicio = "Revisión de frenos",
                fecha = "10/05/2025",
                hora = "12:00",
                estado = "pendiente"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis reservas") },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaReserva) {
                Icon(Icons.Default.Add, contentDescription = "Nueva reserva")
            }
        }
    ) { paddingValues ->
        if (reservas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes reservas todavía")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reservas) { reserva ->
                    TarjetaReserva(reserva = reserva)
                }
            }
        }
    }
}

@Composable
fun TarjetaReserva(reserva: Reserva) {
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
        }
    }
}

@Composable
fun EstadoChip(estado: String) {
    val color = when (estado) {
        "confirmada" -> MaterialTheme.colorScheme.primary
        "cancelada" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = estado.replaceFirstChar { it.uppercase() },
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}