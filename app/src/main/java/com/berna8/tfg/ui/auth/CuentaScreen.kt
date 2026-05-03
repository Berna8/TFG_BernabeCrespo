package com.berna8.tfg.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentaScreen(
    uid: String,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val usuario by viewModel.usuario.collectAsState()
    val perfilActualizado by viewModel.perfilActualizado.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        viewModel.cargarUsuario(uid)
    }

    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre
            nombreUsuario = it.nombreUsuario
        }
    }

    LaunchedEffect(perfilActualizado) {
        if (perfilActualizado) {
            editando = false
            viewModel.resetearPerfilActualizado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi cuenta") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

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
                        onClick = {
                            viewModel.actualizarPerfil(uid, nombre, nombreUsuario)
                        },
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
}