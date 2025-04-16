package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val mp = MediaPlayer.create(context, alert)
        mp?.let { player ->
            player.setVolume(1.0f, 1.0f)
            player.start()
            player.setOnCompletionListener { it.release() }
        }
    }
}