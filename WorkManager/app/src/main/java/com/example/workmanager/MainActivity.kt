package com.example.workmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.workmanager.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.util.*
import androidx.databinding.ObservableField

class WeatherData {
    val cityName = ObservableField<String>("")
    val temperature = ObservableField<String>("")
}

class MainActivity : AppCompatActivity() {

    val weatherData = WeatherData()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this
        binding.mainActivity = this
        binding.wd = weatherData
    }

    fun loadWeatherButtonClicked() {
        val city = binding.etCity.text.toString().trim()
        if (city.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                loadWeather(city)
            }
        } else {
            Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show()
        }
    }
    private suspend fun loadWeather(city: String) {
        val API_KEY = "64117282f3659bef833727f907a3cce3"
        val weatherURL = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$API_KEY&units=metric"

        try {
            val stream = URL(weatherURL).getContent() as InputStream
            val data = Scanner(stream).nextLine()
            parseWeatherData(data)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("error", "Ошибка загрузки данных: ${e.localizedMessage}")
                Toast.makeText(this@MainActivity, "Ошибка загрузки данных: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun parseWeatherData(data: String) {
        val jsonObject = JSONObject(data)
        val cityName = jsonObject.getString("name")
        val temp = jsonObject.getJSONObject("main").getDouble("temp").toString() + " °C"

        withContext(Dispatchers.Main) {
            weatherData.cityName.set(cityName)
            weatherData.temperature.set(temp)
        }
    }
}