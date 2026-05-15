package com.berna8.tfg

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.berna8.tfg.ui.NavegacionApp
import com.berna8.tfg.ui.theme.TFGTheme
import com.berna8.tfg.utils.NotificacionHelper

/**
 * Actividad principal de la aplicación AutoCita.
 * Inicializa el canal de notificaciones, solicita el permiso de notificaciones
 * en Android 13+ y lanza el sistema de navegación Compose.
 */
class MainActivity : ComponentActivity() {

    // Lanzador para solicitar el permiso de notificaciones al usuario
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crea el canal de notificaciones necesario para Android 8+
        NotificacionHelper.crearCanal(this)

        // Solicita permiso de notificaciones en Android 13 (TIRAMISU) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            TFGTheme {
                NavegacionApp()
            }
        }
    }
}