package com.example.fragment

class Weather{
    val coord: Coord = Coord()
    val weather: List<WeatherCondition> = emptyList()
    val base: String = ""
    val main: Main = Main()
    val visibility: Int = 0
    val wind: Wind = Wind()
    val clouds: Clouds = Clouds()
    val dt: Long = 0
    val timezone: Int = 0
    val id: Int = 0
    val name: String = ""
    val cod: Int = 0
}
class Coord{
    val lon: Float = 0.0f
    val lat: Float = 0.0f
}

class WeatherCondition {
    val id: Int = 0
    val main: String = ""
    val description: String = ""
    val icon: String = ""
}

class Main {
    val temp: Float = 0.0f
    val feels_like: Float = 0.0f
    val temp_min: Float = 0.0f
    val temp_max: Float = 0.0f
    val pressure: Int = 0
    val humidity: Int = 0
    val sea_level: Int? = 0
    val grnd_level: Int? = 0
}

class Wind{
    val speed: Float = 0.0f
    val deg: Int = 0
    val gust: Float? = 0.0f
}

class Clouds {
    val all: Int = 0
}

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
