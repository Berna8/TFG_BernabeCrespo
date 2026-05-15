package com.berna8.tfg.ui.reserva

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Pantalla de confirmación que se muestra tras crear una reserva con éxito.
 * Muestra una animación de check y navega automáticamente al inicio tras 3 segundos.
 */
@Composable
fun ConfirmacionReservaScreen(
    servicio: String,
    fecha: String,
    hora: String,
    onContinuar: () -> Unit
) {
    var animacionIniciada by remember { mutableStateOf(false) }

    // Animación de escala con efecto de rebote
    val escala by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "escala"
    )

    // Inicia la animación y navega automáticamente tras 3 segundos
    LaunchedEffect(Unit) {
        animacionIniciada = true
        delay(3000)
        onContinuar()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Icono animado de confirmación
            Surface(
                modifier = Modifier.size(120.dp).scale(escala),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "¡Cita programada!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Tu cita para $servicio el $fecha a las $hora ha sido registrada y está pendiente de confirmación por parte del taller.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}