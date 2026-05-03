package com.berna8.tfg.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.DateRange
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
    object Inicio : ItemNavegacion("inicio", "Inicio", Icons.Default.Home)
    object Buscar : ItemNavegacion("buscar", "Buscar", Icons.Default.Search)
    object Citas : ItemNavegacion("citas", "Citas", Icons.Default.DateRange)
    object Favoritos : ItemNavegacion("favoritos", "Favoritos", Icons.Default.Favorite)
    object Cuenta : ItemNavegacion("cuenta_tab", "Cuenta", Icons.Default.Person)
}

@Composable
fun BarraNavegacionCliente(
    rutaActual: String,
    onItemSeleccionado: (String) -> Unit
) {
    val items = listOf(
        ItemNavegacion.Inicio,
        ItemNavegacion.Buscar,
        ItemNavegacion.Citas,
        ItemNavegacion.Favoritos,
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