package com.berna8.tfg.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

/**
 * Pantalla de cuenta del usuario.
 * Permite ver y editar el perfil, cambiar la foto y cerrar sesión.
 */
@Composable
fun CuentaScreen(
    uid: String,
    onCerrarSesion: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val usuario by viewModel.usuario.collectAsState()
    val perfilActualizado by viewModel.perfilActualizado.collectAsState()
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf(false) }

    // Lanzador para seleccionar imagen de la galería
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.subirFotoPerfil(context, it, uid) }
    }

    LaunchedEffect(uid) {
        viewModel.cargarUsuario(uid)
    }

    // Sincroniza los campos de texto con los datos del usuario cargado
    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre
            nombreUsuario = it.nombreUsuario
        }
    }

    // Cierra el modo edición cuando el perfil se actualiza correctamente
    LaunchedEffect(perfilActualizado) {
        if (perfilActualizado) {
            editando = false
            viewModel.resetearPerfilActualizado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mi cuenta",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Foto de perfil — al pulsar abre la galería
        Surface(
            modifier = Modifier
                .size(100.dp)
                .clickable { launcher.launch("image/*") },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            if (usuario?.fotoPerfil?.isNotBlank() == true) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(usuario?.fotoPerfil)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Text(
            text = "Toca para cambiar foto",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        usuario?.let {
            Text(
                text = it.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "@${it.nombreUsuario}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = it.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        HorizontalDivider()

        if (editando) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = nombreUsuario,
                onValueChange = { nombreUsuario = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { editando = false },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = { viewModel.actualizarPerfil(uid, nombre, nombreUsuario) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar")
                }
            }
        } else {
            Button(
                onClick = { editando = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Editar perfil")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                viewModel.cerrarSesion()
                onCerrarSesion()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Cerrar sesión")
        }
    }
}