package com.example.databinding

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.databinding.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherData = WeatherData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.weather = weatherData
        binding.weatherHandler = WeatherHandler(this, weatherData)
    }

    suspend fun loadWeather(city: String) {
        val API_KEY = "64117282f3659bef833727f907a3cce3"
        val weatherURL = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$API_KEY&units=metric"

        try {
            val stream = URL(weatherURL).getContent() as InputStream
            val data = Scanner(stream).nextLine()
            parseWeatherData(data)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Ошибка загрузки данных: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun parseWeatherData(data: String) {
        val jsonObject = JSONObject(data)
        val cityName = jsonObject.getString("name")
        val temp = jsonObject.getJSONObject("main").getDouble("temp").toString() + "°C"

        withContext(Dispatchers.Main) {
            weatherData.cityName.set(cityName)
            weatherData.temperature.set(temp)
        }
    }
}
