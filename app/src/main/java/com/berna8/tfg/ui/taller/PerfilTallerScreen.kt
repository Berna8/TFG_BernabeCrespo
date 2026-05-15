package com.berna8.tfg.ui.taller

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.berna8.tfg.data.model.Taller

/**
 * Pantalla de gestión del perfil del taller.
 * Permite editar nombre, dirección, teléfono, servicios, horarios e imágenes.
 * El taller solo aparece en la lista de búsqueda cuando tiene el perfil completo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilTallerScreen(
    tallerUid: String,
    viewModel: TallerViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val tallerActual by viewModel.taller.collectAsState()
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var servicios by remember { mutableStateOf(listOf<String>()) }
    var horarios by remember { mutableStateOf(listOf<String>()) }
    var nuevoServicio by remember { mutableStateOf("") }
    var nuevoHorario by remember { mutableStateOf("") }
    var horarioExpandido by remember { mutableStateOf(false) }

    // Horarios predefinidos disponibles para seleccionar
    val horariosPredefinidos = listOf(
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
        "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
        "18:00", "18:30", "19:00"
    )

    // Lanzador para seleccionar imagen de la galería
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.subirImagen(context, it) }
    }

    LaunchedEffect(tallerUid) {
        viewModel.cargarTaller(tallerUid)
    }

    // Sincroniza los campos con los datos del taller cargado
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
            viewModel.resetearEstado()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Mi taller", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = "Datos del taller", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del taller") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

        HorizontalDivider()
        Text(text = "Servicios ofrecidos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Campo para añadir nuevos servicios
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = nuevoServicio, onValueChange = { nuevoServicio = it }, label = { Text("Nuevo servicio") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
            IconButton(onClick = {
                if (nuevoServicio.isNotBlank()) {
                    servicios = servicios + nuevoServicio
                    nuevoServicio = ""
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir servicio")
            }
        }

        // Lista de servicios con botón de eliminar
        servicios.forEach { servicio ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "• $servicio")
                IconButton(onClick = { servicios = servicios - servicio }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }

        HorizontalDivider()
        Text(text = "Horarios disponibles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Selector de horarios predefinidos
        ExposedDropdownMenuBox(expanded = horarioExpandido, onExpandedChange = { horarioExpandido = it }) {
            OutlinedTextField(
                value = nuevoHorario,
                onValueChange = {},
                readOnly = true,
                label = { Text("Añadir horario") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = horarioExpandido) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = horarioExpandido, onDismissRequest = { horarioExpandido = false }) {
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

        // Lista de horarios seleccionados con botón de eliminar
        horarios.forEach { horario ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🕐 $horario")
                IconButton(onClick = { horarios = horarios - horario }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }

        HorizontalDivider()
        Text(text = "Imágenes del taller", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        val imagenes = tallerActual?.imagenes ?: emptyList()

        // Botón para añadir imagen desde la galería
        OutlinedButton(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = estado !is TallerEstado.Cargando
        ) {
            if (estado is TallerEstado.Cargando) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Añadir imagen")
        }

        // Carrusel horizontal de imágenes con botón de eliminar
        if (imagenes.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(imagenes) { url ->
                    Box {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp).clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(onClick = { viewModel.eliminarImagen(url) }, modifier = Modifier.align(Alignment.TopEnd)) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        if (estado is TallerEstado.Error) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                Text(text = (estado as TallerEstado.Error).mensaje, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp))
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
                        horariosDisponibles = horarios,
                        imagenes = imagenes
                    )
                )
            },
            enabled = estado !is TallerEstado.Cargando && nombre.isNotBlank() && direccion.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (estado is TallerEstado.Cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Guardar")
            }
        }
    }
}