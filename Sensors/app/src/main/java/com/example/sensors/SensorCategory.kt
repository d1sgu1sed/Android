package com.example.sensors

enum class SensorCategory(val displayName: String) {
    ENVIRONMENT("Датчики окружающей среды"),
    POSITION("Датчики положения устройства"),
    HUMAN("Датчики состояния человека");

    override fun toString(): String = displayName
}
