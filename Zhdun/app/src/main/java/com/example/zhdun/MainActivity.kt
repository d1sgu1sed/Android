package com.example.zhdun

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.zhdun.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WaitingViewModel

    private var isRegistered = false

    private val tickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_TIME_TICK) {
                viewModel.incrementTime()
            }
        }
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val percent = level!! * 100 / scale!!
            viewModel.setLowBattery(percent < 15)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[WaitingViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        isRegistered = true

        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        binding.button.setOnClickListener {
            if (isRegistered) {
                unregisterReceiver(tickReceiver)
                isRegistered = false
                Toast.makeText(this, getString(R.string.toast_text), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        if (isRegistered) unregisterReceiver(tickReceiver)
        unregisterReceiver(batteryReceiver)
        super.onDestroy()
    }
}
