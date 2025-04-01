package com.example.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.services.R

object NotificationUtils {
    private const val CHANNEL_ID = "rate_changes"
    private const val CHANNEL_NAME = "Rate Change Alerts"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies when rate changes to target"
            }
            val manager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showRateNotification(context: Context, isUp: Boolean, currentRate: String) {
        val arrow = if (isUp) "⬆️" else "⬇️"
        val text = "$arrow Курс ETH достиг $currentRate RUB"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // добавьте иконку в drawable
            .setContentTitle("Изменение курса")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(1, builder.build())
    }
}
