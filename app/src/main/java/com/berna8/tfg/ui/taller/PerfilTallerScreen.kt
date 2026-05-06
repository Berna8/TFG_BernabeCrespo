package com.berna8.tfg.ui.taller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berna8.tfg.data.model.Taller

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilTallerScreen(
    tallerUid: String,
    onGuardado: () -> Unit,
    viewModel: TallerViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val tallerActual by viewModel.taller.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var servicios by remember { mutableStateOf(listOf<String>()) }
    var horarios by remember { mutableStateOf(listOf<String>()) }
    var nuevoServicio by remember { mutableStateOf("") }
    var nuevoHorario by remember { mutableStateOf("") }
    var horarioExpandido by remember { mutableStateOf(false) }

    val horariosPredefinidos = listOf(
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
        "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
        "18:00", "18:30", "19:00"
    )

    LaunchedEffect(tallerUid) {
        viewModel.cargarTaller(tallerUid)
    }

    LaunchedEffect(tallerActual) {
        tallerActual?.let {
            nombre = it.nombre
            direccion = it.direccion
            telefono = it.telefono
            servicios = it.servicios
            horarios = it.horariosDisponibles
        }
    }

    LaunchedEffect(estado) {
        if (estado is TallerEstado.Exito) {
            onGuardado()
            viewModel.resetearEstado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del taller") },
                navigationIcon = {
                    IconButton(onClick = onGuardado) {
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
            // Datos básicos
            Text(
                text = "Datos del taller",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del taller") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            HorizontalDivider()

            // Servicios
            Text(
                text = "Servicios ofrecidos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nuevoServicio,
                    onValueChange = { nuevoServicio = it },
                    label = { Text("Nuevo servicio") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                IconButton(
                    onClick = {
                        if (nuevoServicio.isNotBlank()) {
                            servicios = servicios + nuevoServicio
                            nuevoServicio = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir servicio")
                }
            }

            servicios.forEach { servicio ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "• $servicio")
                    IconButton(onClick = { servicios = servicios - servicio }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            HorizontalDivider()

            // Horarios
            Text(
                text = "Horarios disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ExposedDropdownMenuBox(
                expanded = horarioExpandido,
                onExpandedChange = { horarioExpandido = it }
            ) {
                OutlinedTextField(
                    value = nuevoHorario,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Añadir horario") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = horarioExpandido)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = horarioExpandido,
                    onDismissRequest = { horarioExpandido = false }
                ) {
                    horariosPredefinidos.filter { !horarios.contains(it) }.forEach { horario ->
                        DropdownMenuItem(
                            text = { Text(horario) },
                            onClick = {
                                horarios = (horarios + horario).sorted()
                                nuevoHorario = horario
                                horarioExpandido = false
                            }
                        )
                    }
                }
            }

            horarios.forEach { horario ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🕐 $horario")
                    IconButton(onClick = { horarios = horarios - horario }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (estado is TallerEstado.Error) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (estado as TallerEstado.Error).mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.guardarTaller(
                        Taller(
                            uid = tallerUid,
                            nombre = nombre,
                            direccion = direccion,
                            telefono = telefono,
                            servicios = servicios,
                            horariosDisponibles = horarios
                        )
                    )
                },
                enabled = estado !is TallerEstado.Cargando &&
                        nombre.isNotBlank() &&
                        direccion.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (estado is TallerEstado.Cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}