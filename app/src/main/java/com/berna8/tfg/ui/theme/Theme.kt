package com.berna8.tfg.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Blanco,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondary = NaranjaSecundario,
    onSecondary = Blanco,
    secondaryContainer = NaranjaClaro,
    onSecondaryContainer = NaranjaOscuro,
    background = GrisClaro,
    onBackground = Negro,
    surface = Blanco,
    onSurface = Negro,
    error = Rojo,
    onError = Blanco
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulClaro,
    onPrimary = AzulOscuro,
    primaryContainer = AzulPrimario,
    onPrimaryContainer = Blanco,
    secondary = NaranjaClaro,
    onSecondary = NaranjaOscuro,
    secondaryContainer = NaranjaSecundario,
    onSecondaryContainer = Blanco,
    background = Negro,
    onBackground = Blanco,
    surface = Color(0xFF1E1E1E),
    onSurface = Blanco,
    error = Color(0xFFEF9A9A),
    onError = Rojo
)

@Composable
fun TFGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val esquemaColores = if (darkTheme) EsquemaOscuro else EsquemaClaro

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = esquemaColores.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = esquemaColores,
        typography = Typography,
        content = content
    )
}