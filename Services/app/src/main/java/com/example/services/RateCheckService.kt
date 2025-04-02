package com.example.services

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal

class RateCheckService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var rateCheckAttempt = 0
    private lateinit var startRate: BigDecimal
    private lateinit var targetRate: BigDecimal
    private val rateCheckInteractor = RateCheckInteractor()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private lateinit var rateCheckRunnable: Runnable

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Сервис создаётся (RateCheckService).")

    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: Пришёл вызов startId = $startId")

        startForeground(NOTIF_ID, createForegroundNotification())
        val start = intent?.getStringExtra(ARG_START_RATE)
        val target = intent?.getStringExtra(ARG_TARGET_RATE)

        if (start.isNullOrEmpty() || target.isNullOrEmpty()) {
            Log.e(TAG, "onStartCommand: Не переданы ARG_START_RATE или ARG_TARGET_RATE, остановка сервиса.")
            stopSelf()
            return START_NOT_STICKY
        }

        startRate = try {
            BigDecimal(start)
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Ошибка BigDecimal при парсинге startRate", e)
            stopSelf()
            return START_NOT_STICKY
        }

        targetRate = try {
            BigDecimal(target)
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Ошибка BigDecimal при парсинге targetRate", e)
            stopSelf()
            return START_NOT_STICKY
        }

        Log.d(TAG, "Сервис запущен: старт = $startRate, цель = $targetRate")

        // Инициализация runnable
        rateCheckRunnable = Runnable {
            rateCheckAttempt++
            Log.d(TAG, "rateCheckRunnable: Попытка #$rateCheckAttempt")

            if (rateCheckAttempt > RATE_CHECK_ATTEMPTS_MAX) {
                stopSelf()
                return@Runnable
            }

            scope.launch {
                val rateString = rateCheckInteractor.requestRate()
                Log.d(TAG, "rateCheckRunnable: Текущий курс: $rateString")

                if (rateString.isNotEmpty()) {
                    val currentRate = BigDecimal(rateString)

                    val reached = if (startRate < targetRate) {
                        currentRate >= targetRate
                    } else {
                        currentRate <= targetRate
                    }

                    if (reached) {
                        val isUp = currentRate > startRate
                        Log.d(TAG, "rateCheckRunnable: Цель достигнута. Отправляем уведомление.")
                        NotificationUtils.showRateNotification(this@RateCheckService, isUp, rateString)
                        stopSelf()
                    } else {
                        Log.d(TAG, "rateCheckRunnable: Цель ещё не достигнута. Ждём $RATE_CHECK_INTERVAL мс до следующей попытки.")
                        handler.postDelayed(rateCheckRunnable, RATE_CHECK_INTERVAL)
                    }
                } else {
                    Log.d(TAG, "rateCheckRunnable: Не удалось получить курс (пустой rateString). Повторим попытку через $RATE_CHECK_INTERVAL мс.")
                    handler.postDelayed(rateCheckRunnable, RATE_CHECK_INTERVAL)
                }
            }
        }

        handler.post(rateCheckRunnable)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Сервис уничтожен.")
        handler.removeCallbacks(rateCheckRunnable)
        job.cancel()
    }

    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, NotificationUtils.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Слежение за курсом ETH")
            .setContentText("Фоновый сервис запущен…")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        const val TAG = "RateCheckService"
        const val RATE_CHECK_INTERVAL = 15_000L
        const val RATE_CHECK_ATTEMPTS_MAX = 100
        private const val NOTIF_ID = 101

        const val ARG_START_RATE = "ARG_START_RATE"
        const val ARG_TARGET_RATE = "ARG_TARGET_RATE"

        fun startService(context: Context, startRate: String, targetRate: String) {
            Log.d(TAG, "startService: Запускаем foreground-сервис с $startRate → $targetRate")

            val intent = Intent(context, RateCheckService::class.java).apply {
                putExtra(ARG_START_RATE, startRate)
                putExtra(ARG_TARGET_RATE, targetRate)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, RateCheckService::class.java))
        }
    }
}
