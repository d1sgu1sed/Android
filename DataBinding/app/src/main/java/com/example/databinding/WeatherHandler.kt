package com.example.databinding

import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherHandler(
    private val activity: MainActivity,
    private val weatherData: WeatherData
) {
    @JvmName("onClick")
    fun onClick(view: View) {
        val city = activity.binding.cityInput.text.toString().trim()
        if (city.isEmpty()) {
            Toast.makeText(activity, "Введите название города", Toast.LENGTH_SHORT).show()
            return
        }

        activity.lifecycleScope.launch(Dispatchers.IO) {
            activity.loadWeather(city)
        }
    }

}
