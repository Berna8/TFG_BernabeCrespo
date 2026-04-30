package com.berna8.tfg.ui.reserva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berna8.tfg.data.model.Reserva
import com.berna8.tfg.ui.taller.TallerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaReservaScreen(
    clienteUid: String,
    tallerUid: String,
    onReservaCreada: () -> Unit,
    onVolver: () -> Unit,
    reservaViewModel: ReservaViewModel = viewModel(),
    tallerViewModel: TallerViewModel = viewModel()
) {
    val estado by reservaViewModel.estado.collectAsState()
    val taller by tallerViewModel.taller.collectAsState()

    var servicio by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var servicioExpandido by remember { mutableStateOf(false) }

    LaunchedEffect(tallerUid) {
        tallerViewModel.cargarTaller(tallerUid)
    }

    LaunchedEffect(estado) {
        if (estado is ReservaEstado.Exito) {
            onReservaCreada()
            reservaViewModel.resetearEstado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva reserva") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            taller?.let {
                Text(
                    text = it.nombre,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = it.direccion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Selecciona el servicio",
                style = MaterialTheme.typography.titleMedium
            )

            val servicios = taller?.servicios ?: emptyList()

            ExposedDropdownMenuBox(
                expanded = servicioExpandido,
                onExpandedChange = { servicioExpandido = it }
            ) {
                OutlinedTextField(
                    value = servicio,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Servicio") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = servicioExpandido)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = servicioExpandido,
                    onDismissRequest = { servicioExpandido = false }
                ) {
                    servicios.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s) },
                            onClick = {
                                servicio = s
                                servicioExpandido = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (DD/MM/AAAA)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = hora,
                onValueChange = { hora = it },
                label = { Text("Hora (HH:MM)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (estado is ReservaEstado.Error) {
                Text(
                    text = (estado as ReservaEstado.Error).mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    reservaViewModel.crearReserva(
                        Reserva(
                            clienteUid = clienteUid,
                            tallerUid = tallerUid,
                            servicio = servicio,
                            fecha = fecha,
                            hora = hora
                        )
                    )
                },
                enabled = estado !is ReservaEstado.Cargando &&
                        servicio.isNotBlank() &&
                        fecha.isNotBlank() &&
                        hora.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estado is ReservaEstado.Cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Confirmar reserva")
                }
            }
        }
    }
}