package com.berna8.tfg.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.berna8.tfg.ui.auth.CuentaScreen
import com.berna8.tfg.ui.home.HomeClienteScreen
import com.berna8.tfg.ui.reserva.HistorialCitasScreen
import com.berna8.tfg.ui.taller.BuscarTalleresScreen
import com.berna8.tfg.ui.taller.FavoritosScreen

@Composable
fun PantallaClientePrincipal(
    clienteUid: String,
    onCerrarSesion: () -> Unit,
    onTallerSeleccionado: (String) -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: ItemNavegacion.Inicio.ruta

    Scaffold(
        bottomBar = {
            BarraNavegacionCliente(
                rutaActual = rutaActual,
                onItemSeleccionado = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(ItemNavegacion.Inicio.ruta) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ItemNavegacion.Inicio.ruta,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(ItemNavegacion.Inicio.ruta) {
                HomeClienteScreen(
                    clienteUid = clienteUid
                )
            }

            composable(ItemNavegacion.Buscar.ruta) {
                BuscarTalleresScreen(
                    clienteUid = clienteUid,
                    onTallerSeleccionado = { tallerUid ->
                        onTallerSeleccionado(tallerUid)
                    }
                )
            }

            composable(ItemNavegacion.Citas.ruta) {
                HistorialCitasScreen(
                    clienteUid = clienteUid,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }

            composable(ItemNavegacion.Favoritos.ruta) {
                FavoritosScreen(
                    clienteUid = clienteUid,
                    onTallerSeleccionado = { tallerUid ->
                        onTallerSeleccionado(tallerUid)
                    }
                )
            }

            composable(ItemNavegacion.Cuenta.ruta) {
                CuentaScreen(
                    uid = clienteUid,
                    onCerrarSesion = onCerrarSesion,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}