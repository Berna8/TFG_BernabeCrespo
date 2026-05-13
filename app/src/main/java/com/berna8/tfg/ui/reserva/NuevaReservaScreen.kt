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
    onReservaCreada: (String, String, String) -> Unit,
    onVolver: () -> Unit,
    reservaViewModel: ReservaViewModel = viewModel(),
    tallerViewModel: TallerViewModel = viewModel()
) {
    val estado by reservaViewModel.estado.collectAsState()
    val taller by tallerViewModel.taller.collectAsState()
    val horasOcupadas by reservaViewModel.horasOcupadas.collectAsState()

    var servicio by remember { mutableStateOf("") }
    var marcaCoche by remember { mutableStateOf("") }
    var modeloCoche by remember { mutableStateOf("") }
    var matriculaCoche by remember { mutableStateOf("") }
    var servicioExpandido by remember { mutableStateOf(false) }
    var horaExpandida by remember { mutableStateOf(false) }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }
    var horaSeleccionada by remember { mutableStateOf<LocalTime?>(null) }

    val fechaFormateada = fechaSeleccionada?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
    val horaFormateada = horaSeleccionada?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis() - 86400000
            }
        }
    )

    LaunchedEffect(tallerUid) {
        tallerViewModel.cargarTaller(tallerUid)
    }

    LaunchedEffect(fechaFormateada, servicio) {
        if (fechaFormateada.isNotBlank() && servicio.isNotBlank()) {
            reservaViewModel.cargarHorasOcupadas(tallerUid, fechaFormateada, servicio)
            horaSeleccionada = null
        }
    }

    LaunchedEffect(estado) {
        if (estado is ReservaEstado.Exito) {
            onReservaCreada(servicio, fechaFormateada, horaFormateada)
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
                        val fechaStr = fechaSeleccionada?.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ) ?: ""
                        if (servicio.isNotBlank()) {
                            reservaViewModel.cargarHorasOcupadas(tallerUid, fechaStr, servicio)
                        }
                        horaSeleccionada = null
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

            val ahora = LocalTime.now()
            val horasDisponibles = (taller?.horariosDisponibles ?: emptyList())
                .filter { hora ->
                    !horasOcupadas.contains(hora) &&
                            (fechaSeleccionada?.isAfter(LocalDate.now()) == true ||
                                    LocalTime.parse(hora).isAfter(ahora))
                }

            ExposedDropdownMenuBox(
                expanded = horaExpandida,
                onExpandedChange = { if (fechaFormateada.isNotBlank()) horaExpandida = it }
            ) {
                OutlinedTextField(
                    value = horaFormateada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(if (fechaFormateada.isBlank()) "Selecciona primero una fecha" else "Hora") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = horaExpandida)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp),
                    enabled = fechaFormateada.isNotBlank()
                )
                ExposedDropdownMenu(
                    expanded = horaExpandida,
                    onDismissRequest = { horaExpandida = false }
                ) {
                    if (horasDisponibles.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay horas disponibles") },
                            onClick = { horaExpandida = false }
                        )
                    } else {
                        horasDisponibles.forEach { hora ->
                            DropdownMenuItem(
                                text = { Text(hora) },
                                onClick = {
                                    horaSeleccionada = LocalTime.parse(hora)
                                    horaExpandida = false
                                }
                            )
                        }
                    }
                }
            }

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