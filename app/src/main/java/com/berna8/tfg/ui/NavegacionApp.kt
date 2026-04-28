package com.berna8.tfg.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.berna8.tfg.ui.auth.LoginScreen
import com.berna8.tfg.ui.auth.RegistroScreen

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val HOME_CLIENTE = "home_cliente"
    const val HOME_TALLER = "home_taller"
}

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { rol ->
                    if (rol == "taller") {
                        navController.navigate(Rutas.HOME_TALLER) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Rutas.HOME_CLIENTE) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    }
                },
                onIrARegistro = {
                    navController.navigate(Rutas.REGISTRO)
                }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = { rol ->
                    if (rol == "taller") {
                        navController.navigate(Rutas.HOME_TALLER) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Rutas.HOME_CLIENTE) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    }
                },
                onIrALogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Rutas.HOME_CLIENTE) {
            // Próximamente
        }

        composable(Rutas.HOME_TALLER) {
            // Próximamente
        }
    }
}