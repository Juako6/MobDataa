package com.example.mobdata.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// ==========================================
// RECURSO NATIVO: notificaciones del dispositivo.
// Tiene sentido en esta app porque confirma el check-in guardado
// aunque el usuario esté fuera de la aplicación (registro diario).
// ==========================================
object Notificaciones {

    private const val CANAL_ID = "canal_checkin"
    private const val NOTIFICACION_ID = 1001

    fun confirmarCheckIn(context: Context, emocion: String) {
        crearCanal(context)

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Check-in guardado")
            .setContentText("Tu registro de \"$emocion\" quedó guardado en el dispositivo.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICACION_ID, notificacion)
    }

    private fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Confirmaciones de check-in",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Confirma que el registro de ánimo fue guardado localmente"
            }
            NotificationManagerCompat.from(context).createNotificationChannel(canal)
        }
    }
}
