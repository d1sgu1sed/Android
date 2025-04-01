package com.example.databinding

import androidx.databinding.ObservableField

data class WeatherData(
    val cityName: ObservableField<String> = ObservableField(""),
    val temperature: ObservableField<String> = ObservableField("")
)