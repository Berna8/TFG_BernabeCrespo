package com.berna8.tfg.ui.reserva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berna8.tfg.data.model.Reserva
import com.berna8.tfg.ui.taller.TallerViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
    var marcaCoche by remember { mutableStateOf("") }
    var modeloCoche by remember { mutableStateOf("") }
    var matriculaCoche by remember { mutableStateOf("") }
    var servicioExpandido by remember { mutableStateOf(false) }

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }

    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }
    var horaSeleccionada by remember { mutableStateOf<LocalTime?>(null) }

    val fechaFormateada = fechaSeleccionada?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
    val horaFormateada = horaSeleccionada?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    LaunchedEffect(tallerUid) {
        tallerViewModel.cargarTaller(tallerUid)
    }

    LaunchedEffect(estado) {
        if (estado is ReservaEstado.Exito) {
            onReservaCreada()
            reservaViewModel.resetearEstado()
        }
    }

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        fechaSeleccionada = LocalDate.ofEpochDay(millis / 86400000)
                    }
                    mostrarDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (mostrarTimePicker) {
        AlertDialog(
            onDismissRequest = { mostrarTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    horaSeleccionada = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    mostrarTimePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva reserva") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            taller?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = it.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = it.direccion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Text(
                text = "Servicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
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
                    label = { Text("Selecciona un servicio") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = servicioExpandido)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp)
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

            Text(
                text = "Fecha y hora",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = fechaFormateada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha") },
                trailingIcon = {
                    IconButton(onClick = { mostrarDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = horaFormateada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hora") },
                trailingIcon = {
                    IconButton(onClick = { mostrarTimePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar hora")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                text = "Datos del vehículo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = marcaCoche,
                onValueChange = { marcaCoche = it },
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = modeloCoche,
                onValueChange = { modeloCoche = it },
                label = { Text("Modelo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = matriculaCoche,
                onValueChange = { matriculaCoche = it },
                label = { Text("Matrícula") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (estado is ReservaEstado.Error) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (estado as ReservaEstado.Error).mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    reservaViewModel.crearReserva(
                        Reserva(
                            clienteUid = clienteUid,
                            tallerUid = tallerUid,
                            servicio = servicio,
                            fecha = fechaFormateada,
                            hora = horaFormateada,
                            marcaCoche = marcaCoche,
                            modeloCoche = modeloCoche,
                            matriculaCoche = matriculaCoche
                        )
                    )
                },
                enabled = estado !is ReservaEstado.Cargando &&
                        servicio.isNotBlank() &&
                        fechaFormateada.isNotBlank() &&
                        horaFormateada.isNotBlank() &&
                        marcaCoche.isNotBlank() &&
                        modeloCoche.isNotBlank() &&
                        matriculaCoche.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (estado is ReservaEstado.Cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirmar reserva")
                }
            }
        }
    }
}