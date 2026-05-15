package com.berna8.tfg.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.berna8.tfg.ui.auth.CuentaScreen
import com.berna8.tfg.ui.reserva.HistorialCitasScreen
import com.berna8.tfg.ui.reserva.ReservaViewModel
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
    val rutaActual = backStackEntry?.destination?.route ?: ItemNavegacion.Buscar.ruta
    val context = LocalContext.current
    val reservaViewModel: ReservaViewModel = viewModel()

    LaunchedEffect(clienteUid) {
        reservaViewModel.comprobarNotificaciones(clienteUid, context)
    }

    Scaffold(
        bottomBar = {
            BarraNavegacionCliente(
                rutaActual = rutaActual,
                onItemSeleccionado = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(ItemNavegacion.Buscar.ruta) {
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
            startDestination = ItemNavegacion.Buscar.ruta,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(ItemNavegacion.Buscar.ruta) {
                BuscarTalleresScreen(
                    clienteUid = clienteUid,
                    onTallerSeleccionado = { tallerUid ->
                        onTallerSeleccionado(tallerUid)
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

            composable(ItemNavegacion.Citas.ruta) {
                HistorialCitasScreen(
                    clienteUid = clienteUid,
                    onVolver = {
                        navController.popBackStack()
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