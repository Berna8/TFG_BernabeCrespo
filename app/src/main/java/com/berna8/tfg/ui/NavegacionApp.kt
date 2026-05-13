package com.berna8.tfg.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.berna8.tfg.data.repository.AuthRepository
import com.berna8.tfg.ui.auth.*
import com.berna8.tfg.ui.home.HomeClienteScreen
import com.berna8.tfg.ui.home.HomeTallerScreen
import com.berna8.tfg.ui.reserva.HistorialCitasScreen
import com.berna8.tfg.ui.reserva.NuevaReservaScreen
import com.berna8.tfg.ui.taller.ListaTalleresScreen
import com.berna8.tfg.ui.taller.PerfilTallerScreen
import com.google.firebase.auth.FirebaseAuth
import com.berna8.tfg.ui.PantallaClientePrincipal
import com.berna8.tfg.ui.reserva.ConfirmacionReservaScreen
import com.berna8.tfg.ui.taller.DetalleTallerScreen
import com.berna8.tfg.ui.PantallaTallerPrincipal

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
    const val CARGANDO = "cargando"
    const val CLIENTE_PRINCIPAL = "cliente_principal/{uid}"
    const val CONFIRMACION_RESERVA = "confirmacion_reserva/{uid}/{servicio}/{fecha}/{hora}"
    const val DETALLE_TALLER = "detalle_taller/{uid}/{tallerUid}"
    const val TALLER_PRINCIPAL = "taller_principal/{uid}"
}

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()
    val usuarioActual = auth.currentUser

    val startDestination = if (usuarioActual != null && usuarioActual.isEmailVerified) {
        Rutas.CARGANDO
    } else {
        Rutas.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Rutas.CARGANDO) {
            val uid = usuarioActual?.uid ?: ""
            LaunchedEffect(Unit) {
                val rol = AuthRepository().obtenerRol(uid)
                if (rol == "taller") {
                    navController.navigate("taller_principal/$uid") {
                        popUpTo(Rutas.CARGANDO) { inclusive = true }
                    }
                } else {
                    navController.navigate("cliente_principal/$uid") {
                        popUpTo(Rutas.CARGANDO) { inclusive = true }
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { rol, uid ->
                    if (rol == "taller") {
                        navController.navigate("taller_principal/$uid") {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate("cliente_principal/$uid") {
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

        composable(Rutas.CLIENTE_PRINCIPAL) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            PantallaClientePrincipal(
                clienteUid = uid,
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTallerSeleccionado = { tallerUid ->
                    navController.navigate("detalle_taller/$uid/$tallerUid")
                }
            )
        }

        composable(Rutas.TALLER_PRINCIPAL) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            PantallaTallerPrincipal(
                tallerUid = uid,
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.LISTA_TALLERES) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            ListaTalleresScreen(
                clienteUid = uid,
                onTallerSeleccionado = { tallerUid ->
                    navController.navigate("nueva_reserva/$uid/$tallerUid")
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(Rutas.NUEVA_RESERVA) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            val tallerUid = backStackEntry.arguments?.getString("tallerUid") ?: ""
            NuevaReservaScreen(
                clienteUid = uid,
                tallerUid = tallerUid,
                onReservaCreada = { servicio, fecha, hora ->
                    val servicioEncoded = java.net.URLEncoder.encode(servicio, "UTF-8")
                    val fechaEncoded = java.net.URLEncoder.encode(fecha, "UTF-8")
                    val horaEncoded = java.net.URLEncoder.encode(hora, "UTF-8")
                    navController.navigate("confirmacion_reserva/$uid/$servicioEncoded/$fechaEncoded/$horaEncoded") {
                        popUpTo("nueva_reserva/$uid/$tallerUid") { inclusive = true }
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
                onVolver = {
                    navController.popBackStack()
                },
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

        composable(Rutas.CONFIRMACION_RESERVA) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            val servicio = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("servicio") ?: "", "UTF-8")
            val fecha = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("fecha") ?: "", "UTF-8")
            val hora = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("hora") ?: "", "UTF-8")
            ConfirmacionReservaScreen(
                servicio = servicio,
                fecha = fecha,
                hora = hora,
                onContinuar = {
                    navController.navigate("cliente_principal/$uid") {
                        popUpTo("cliente_principal/$uid") { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.DETALLE_TALLER) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            val tallerUid = backStackEntry.arguments?.getString("tallerUid") ?: ""
            val authViewModel: AuthViewModel = viewModel()
            val usuario by authViewModel.usuario.collectAsState()

            LaunchedEffect(uid) {
                authViewModel.cargarUsuario(uid)
            }

            DetalleTallerScreen(
                tallerUid = tallerUid,
                clienteUid = uid,
                clienteNombre = usuario?.nombre ?: "",
                onReservar = {
                    navController.navigate("nueva_reserva/$uid/$tallerUid")
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}