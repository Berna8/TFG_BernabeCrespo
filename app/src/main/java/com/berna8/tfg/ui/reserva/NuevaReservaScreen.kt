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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaReservaScreen(
    clienteUid: String,
    onReservaCreada: () -> Unit,
    onVolver: () -> Unit,
    viewModel: ReservaViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()

    var servicio by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var servicioExpandido by remember { mutableStateOf(false) }

    val servicios = listOf(
        "Cambio de aceite",
        "Revisión de frenos",
        "Cambio de neumáticos",
        "Revisión general",
        "Cambio de batería",
        "Alineación y equilibrado"
    )

    LaunchedEffect(estado) {
        if (estado is ReservaEstado.Exito) {
            onReservaCreada()
            viewModel.resetearEstado()
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
            Text(
                text = "Selecciona el servicio",
                style = MaterialTheme.typography.titleMedium
            )

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
                    viewModel.crearReserva(
                        Reserva(
                            clienteUid = clienteUid,
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