package com.berna8.tfg.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.berna8.tfg.ui.auth.AuthViewModel
import com.berna8.tfg.ui.auth.CuentaScreen
import com.berna8.tfg.ui.auth.LoginScreen
import com.berna8.tfg.ui.auth.RegistroScreen
import com.berna8.tfg.ui.auth.VerificacionEmailScreen
import com.berna8.tfg.ui.home.HomeClienteScreen
import com.berna8.tfg.ui.home.HomeTallerScreen
import com.berna8.tfg.ui.reserva.NuevaReservaScreen
import com.berna8.tfg.ui.taller.ListaTalleresScreen
import com.berna8.tfg.ui.taller.PerfilTallerScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berna8.tfg.ui.reserva.HistorialCitasScreen

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val VERIFICACION_EMAIL = "verificacion_email"
    const val HOME_CLIENTE = "home_cliente/{uid}"
    const val HOME_TALLER = "home_taller/{uid}"
    const val NUEVA_RESERVA = "nueva_reserva/{uid}/{tallerUid}"
    const val PERFIL_TALLER = "perfil_taller/{uid}"
    const val LISTA_TALLERES = "lista_talleres/{uid}"
    const val CUENTA = "cuenta/{uid}"

    const val HISTORIAL_CITAS = "historial_citas/{uid}"
}

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { rol, uid ->
                    if (rol == "taller") {
                        navController.navigate("home_taller/$uid") {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate("home_cliente/$uid") {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    }
                },
                onIrARegistro = {
                    navController.navigate(Rutas.REGISTRO)
                },
                onEmailNoVerificado = {
                    navController.navigate(Rutas.VERIFICACION_EMAIL)
                },
                viewModel = authViewModel
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = { _, _ ->
                    navController.navigate(Rutas.VERIFICACION_EMAIL) {
                        popUpTo(Rutas.REGISTRO) { inclusive = true }
                    }
                },
                onIrALogin = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }

        composable(Rutas.VERIFICACION_EMAIL) {
            VerificacionEmailScreen(
                onEmailVerificado = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.VERIFICACION_EMAIL) { inclusive = true }
                    }
                },
                onVolver = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Rutas.HOME_CLIENTE) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            HomeClienteScreen(
                clienteUid = uid,
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNuevaReserva = {
                    navController.navigate("lista_talleres/$uid")
                },
                onIrACuenta = {
                    navController.navigate("cuenta/$uid")
                },
                        onVerHistorial = {
                    navController.navigate("historial_citas/$uid")
                }
            )
        }

        composable(Rutas.HOME_TALLER) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            HomeTallerScreen(
                tallerUid = uid,
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onEditarPerfil = {
                    navController.navigate("perfil_taller/$uid")
                }
            )
        }

        composable(Rutas.LISTA_TALLERES) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            ListaTalleresScreen(
                onTallerSeleccionado = { tallerUid ->
                    navController.navigate("nueva_reserva/$uid/$tallerUid")
                }
            )
        }

        composable(Rutas.NUEVA_RESERVA) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            val tallerUid = backStackEntry.arguments?.getString("tallerUid") ?: ""
            NuevaReservaScreen(
                clienteUid = uid,
                tallerUid = tallerUid,
                onReservaCreada = {
                    navController.navigate("home_cliente/$uid") {
                        popUpTo("home_cliente/$uid") { inclusive = true }
                    }
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(Rutas.PERFIL_TALLER) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            PerfilTallerScreen(
                tallerUid = uid,
                onGuardado = {
                    navController.popBackStack()
                }
            )
        }

        composable(Rutas.CUENTA) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            CuentaScreen(
                uid = uid,
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.HISTORIAL_CITAS) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            HistorialCitasScreen(
                clienteUid = uid,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}