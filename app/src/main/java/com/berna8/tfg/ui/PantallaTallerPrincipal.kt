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
import com.berna8.tfg.ui.home.HomeTallerScreen
import com.berna8.tfg.ui.home.HistorialTallerScreen
import com.berna8.tfg.ui.taller.PerfilTallerScreen

@Composable
fun PantallaTallerPrincipal(
    tallerUid: String,
    onCerrarSesion: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route ?: ItemNavegacionTaller.Citas.ruta

    Scaffold(
        bottomBar = {
            BarraNavegacionTaller(
                rutaActual = rutaActual,
                onItemSeleccionado = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(ItemNavegacionTaller.Citas.ruta) {
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
            startDestination = ItemNavegacionTaller.Citas.ruta,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(ItemNavegacionTaller.Citas.ruta) {
                HomeTallerScreen(
                    tallerUid = tallerUid,
                    onCerrarSesion = onCerrarSesion,
                    onEditarPerfil = {
                        navController.navigate(ItemNavegacionTaller.MiTaller.ruta)
                    },
                    onIrACuenta = {
                        navController.navigate(ItemNavegacionTaller.Cuenta.ruta)
                    }
                )
            }

            composable(ItemNavegacionTaller.Historial.ruta) {
                HistorialTallerScreen(
                    tallerUid = tallerUid
                )
            }

            composable(ItemNavegacionTaller.MiTaller.ruta) {
                PerfilTallerScreen(
                    tallerUid = tallerUid,
                    onGuardado = {
                        navController.popBackStack()
                    }
                )
            }

            composable(ItemNavegacionTaller.Cuenta.ruta) {
                CuentaScreen(
                    uid = tallerUid,
                    onCerrarSesion = onCerrarSesion,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}