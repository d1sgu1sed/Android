package com.example.recyclerview

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    //val planetsList = arrayListOf<String>("Mars", "Venus", "Earth")
    // TODO: реализовать генерацию цветов определённой палитры
    val colorPalettes = listOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.MAGENTA,
        Color.CYAN
    )

    // Выбираем случайный цвет из палитры
    val baseColor = colorPalettes.random()

    // Генерируем 5 случайных оттенков этого цвета
    val colorsList = generateShades(baseColor, 5)
//    val colorsList = mutableListOf(Color.YELLOW, Color.RED, Color.GREEN, Color.MAGENTA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // пример использования ListView
//        val lv = findViewById<ListView>(R.id.list)
//        val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, planetsList)
//        lv.adapter = adapter

        // пример использования RecyclerView с собственным адаптером
        val rv = findViewById<RecyclerView>(R.id.rview)
        val colorAdapter = ColorAdapter(LayoutInflater.from(this))
        // добавляем данные в список для отображения
        colorAdapter.submitList(colorsList)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = colorAdapter

    }

    fun generateShades(color: Int, count: Int): List<Int> {
        val shades = mutableListOf<Int>()

        for (i in 1..count) {
            // Генерируем оттенок, изменяя яркость
            val factor = Random.nextFloat() * 0.5f + 0.5f // Генерируем фактор от 0.5 до 1.0
            val shade = adjustColorBrightness(color, factor)
            shades.add(shade)
        }

        return shades
    }

    // Функция для изменения яркости цвета
    fun adjustColorBrightness(color: Int, factor: Float): Int {
        val red = (Color.red(color) * factor).toInt().coerceIn(0, 255)
        val green = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val blue = (Color.blue(color) * factor).toInt().coerceIn(0, 255)

        return Color.rgb(red, green, blue)
    }

}