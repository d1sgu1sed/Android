package com.example.alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private var alarmPendingIntent: PendingIntent? = null

    private var notifyPendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun oneAlarm(view: android.view.View) {
        val intent = Intent(this, ArtActivity::class.java)

        val penIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        am.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 15000,
            penIntent
        )

        Toast.makeText(
            applicationContext,
            "Терпение! Через 15 секунд всё будет!",
            Toast.LENGTH_LONG
        ).show()
    }

    fun repeatAlarm(view: android.view.View) {
        val timePicker = TimePickerFragment()
        timePicker.show(supportFragmentManager, "picker")
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        val alarmIntent = Intent(this, AlarmReceiver::class.java)

        val penIntent = PendingIntent.getBroadcast(
            this,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        am.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000,
            penIntent
        )

        Toast.makeText(
            this,
            "Будильник установлен на $hourOfDay:${minute.toString().padStart(2, '0')}",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun clearRep(view: android.view.View) {
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val penIntent = PendingIntent.getBroadcast(
            this,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(penIntent)

        Toast.makeText(this, "Будильник отключён", Toast.LENGTH_SHORT).show()
    }

    fun setNotify(view: android.view.View) {
        createNotificationChannel()

        val notifyIntent = Intent(this, AlarmReceiverNotify::class.java)

        val penIntent = PendingIntent.getBroadcast(
            this,
            1,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        am.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime(),
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            penIntent
        )

        Toast.makeText(this, "Уведомления включены (каждые 15 минут)", Toast.LENGTH_SHORT).show()
    }


    private fun createNotificationChannel() {
        val CID = "TestNotify"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun stopNotify(view: android.view.View) {
        val notifyIntent = Intent(this, AlarmReceiverNotify::class.java)
        val penIntent = PendingIntent.getBroadcast(
            this,
            1,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(penIntent)

        Toast.makeText(this, "Уведомления отключены", Toast.LENGTH_SHORT).show()
    }
}