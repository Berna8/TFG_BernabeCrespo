package com.berna8.tfg.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ItemNavegacion(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
) {
    object Buscar : ItemNavegacion("buscar", "Buscar", Icons.Default.Search)
    object Favoritos : ItemNavegacion("favoritos", "Favoritos", Icons.Default.Favorite)
    object Citas : ItemNavegacion("citas", "Citas", Icons.Default.DateRange)
    object Cuenta : ItemNavegacion("cuenta_tab", "Cuenta", Icons.Default.AccountCircle)
}

sealed class ItemNavegacionTaller(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
) {
    object Citas : ItemNavegacionTaller("taller_citas", "Citas", Icons.Default.DateRange)
    object Historial : ItemNavegacionTaller("taller_historial", "Historial", Icons.AutoMirrored.Filled.List)
    object MiTaller : ItemNavegacionTaller("taller_perfil", "Mi taller", Icons.Default.Build)
    object Cuenta : ItemNavegacionTaller("taller_cuenta", "Cuenta", Icons.Default.AccountCircle)
}

@Composable
fun BarraNavegacionCliente(
    rutaActual: String,
    onItemSeleccionado: (String) -> Unit
) {
    val items = listOf(
        ItemNavegacion.Buscar,
        ItemNavegacion.Favoritos,
        ItemNavegacion.Citas,
        ItemNavegacion.Cuenta
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icono, contentDescription = item.titulo) },
                label = { Text(item.titulo) },
                selected = rutaActual == item.ruta,
                onClick = { onItemSeleccionado(item.ruta) }
            )
        }
    }
}

@Composable
fun BarraNavegacionTaller(
    rutaActual: String,
    onItemSeleccionado: (String) -> Unit
) {
    val items = listOf(
        ItemNavegacionTaller.Citas,
        ItemNavegacionTaller.Historial,
        ItemNavegacionTaller.MiTaller,
        ItemNavegacionTaller.Cuenta
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icono, contentDescription = item.titulo) },
                label = { Text(item.titulo) },
                selected = rutaActual == item.ruta,
                onClick = { onItemSeleccionado(item.ruta) }
            )
        }
    }
}