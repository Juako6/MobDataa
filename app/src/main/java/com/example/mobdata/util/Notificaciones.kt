package com.example.mobdata.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// ==========================================
// RECURSO NATIVO: notificaciones del dispositivo.
// Confirma el guardado aunque el usuario salga de la app.
// REGLA DE NEGOCIO: las alertas son neutrales y genéricas; nunca incluyen
// emociones, habilidades ni términos sensibles, y se ocultan en la
// pantalla de bloqueo (VISIBILITY_SECRET).
// ==========================================
object Notificaciones {

    private const val CANAL_ID = "canal_registro"
    private const val NOTIFICACION_ID = 1001

    fun confirmarRegistro(context: Context) {
        crearCanal(context)

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("MobData")
            .setContentText("Registro guardado correctamente.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICACION_ID, notificacion)
    }

    private fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Confirmaciones de registro",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Confirma que un registro fue guardado en el dispositivo"
                lockscreenVisibility = Notification.VISIBILITY_SECRET
            }
            NotificationManagerCompat.from(context).createNotificationChannel(canal)
        }
    }
}
