package com.example.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import java.math.BigDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RateCheckService : Service() {
    val handler = Handler(Looper.getMainLooper())
    var rateCheckAttempt = 0
    lateinit var startRate: BigDecimal
    lateinit var targetRate: BigDecimal
    val rateCheckInteractor = RateCheckInteractor()

    val rateCheckRunnable: Runnable = Runnable {
        requestAndCheckRate()
    }

    private fun requestAndCheckRate() {

        rateCheckAttempt++
        if (rateCheckAttempt > RATE_CHECK_ATTEMPTS_MAX) {
            stopSelf()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val rateString = rateCheckInteractor.requestRate()
            if (rateString.isNotEmpty()) {
                val currentRate = BigDecimal(rateString)
                Log.d(TAG, "Проверка курса: $currentRate")

                val reached = if (startRate < targetRate) {
                    currentRate >= targetRate
                } else {
                    currentRate <= targetRate
                }

                if (reached) {
                    val isUp = currentRate > startRate
                    NotificationUtils.showRateNotification(this@RateCheckService, isUp, rateString)
                    stopSelf()
                } else {
                    handler.postDelayed(rateCheckRunnable, RATE_CHECK_INTERVAL)
                }
            } else {
                handler.postDelayed(rateCheckRunnable, RATE_CHECK_INTERVAL)
            }
        }
    }


    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRate = BigDecimal(intent?.getStringExtra(ARG_START_RATE))
        targetRate = BigDecimal(intent?.getStringExtra(ARG_TARGET_RATE))

        Log.d(TAG, "Сервис запущен: $startRate → $targetRate")


        handler.post(rateCheckRunnable)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(rateCheckRunnable)
    }


    companion object {
        const val TAG = "RateCheckService"
        const val RATE_CHECK_INTERVAL = 5000L
        const val RATE_CHECK_ATTEMPTS_MAX = 100

        const val ARG_START_RATE = "ARG_START_RATE"
        const val ARG_TARGET_RATE = "ARG_TARGET_RATE"

        fun startService(context: Context, startRate: String, targetRate: String) {
            context.startService(Intent(context, RateCheckService::class.java).apply {
                putExtra(ARG_START_RATE, startRate)
                putExtra(ARG_TARGET_RATE, targetRate)
            })
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, RateCheckService::class.java))
        }
    }



}