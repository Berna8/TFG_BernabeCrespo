package com.berna8.tfg.ui.taller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berna8.tfg.data.model.Taller
import androidx.compose.material.icons.automirrored.filled.ArrowBack

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
    var nuevoServicio by remember { mutableStateOf("") }

    LaunchedEffect(tallerUid) {
        viewModel.cargarTaller(tallerUid)
    }

    LaunchedEffect(tallerActual) {
        tallerActual?.let {
            nombre = it.nombre
            direccion = it.direccion
            telefono = it.telefono
            servicios = it.servicios
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
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del taller") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Servicios ofrecidos",
                style = MaterialTheme.typography.titleMedium
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
                    modifier = Modifier.weight(1f)
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
                    IconButton(
                        onClick = {
                            servicios = servicios - servicio
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar servicio",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (estado is TallerEstado.Error) {
                Text(
                    text = (estado as TallerEstado.Error).mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    viewModel.guardarTaller(
                        Taller(
                            uid = tallerUid,
                            nombre = nombre,
                            direccion = direccion,
                            telefono = telefono,
                            servicios = servicios
                        )
                    )
                },
                enabled = estado !is TallerEstado.Cargando &&
                        nombre.isNotBlank() &&
                        direccion.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estado is TallerEstado.Cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}