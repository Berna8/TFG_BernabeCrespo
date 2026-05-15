package com.berna8.tfg.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.berna8.tfg.R

/**
 * Helper para gestionar las notificaciones locales de la aplicación.
 * Encapsula la creación del canal y el envío de notificaciones.
 */
object NotificacionHelper {

    private const val CANAL_ID = "autocita_canal"
    private const val CANAL_NOMBRE = "AutoCita"

    /**
     * Crea el canal de notificaciones requerido en Android 8 (Oreo) o superior.
     * Debe llamarse al iniciar la app antes de mostrar cualquier notificación.
     */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                CANAL_NOMBRE,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de AutoCita"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    /**
     * Muestra una notificación local con el título y mensaje indicados.
     * Usa un ID único basado en el timestamp para evitar sobreescribir notificaciones previas.
     */
    fun mostrarNotificacion(
        context: Context,
        titulo: String,
        mensaje: String,
        id: Int = System.currentTimeMillis().toInt()
    ) {
        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(id, notificacion)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}