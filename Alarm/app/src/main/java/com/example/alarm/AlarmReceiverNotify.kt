package com.example.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiverNotify : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val CID = "TestNotify"

        val penIntent = android.app.PendingIntent.getActivity(
            context,
            1,
            Intent(context, ArtActivity::class.java),
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Вам письмо!")
            .setContentText("Примите самые искренние пожелания добра и удачи!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(penIntent)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(102, builder)
        }
    }
}
