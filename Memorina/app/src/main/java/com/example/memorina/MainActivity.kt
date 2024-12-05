package com.example.memorina

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val images = ArrayList<Int>()
    private var firstCard: ImageView? = null
    private var secondCard: ImageView? = null
    private var isClickable = true
    private val openedPairs = mutableListOf<View>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        val layout = LinearLayout(applicationContext)

        layout.orientation = LinearLayout.VERTICAL

        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.weight = 1.toFloat() // единичный вес

        images.add(R.drawable.pinout_first)
        images.add(R.drawable.pinout_first)
        images.add(R.drawable.pinout_second)
        images.add(R.drawable.pinout_second)
        images.add(R.drawable.pinout_third)
        images.add(R.drawable.pinout_third)
        images.add(R.drawable.pinout_fourth)
        images.add(R.drawable.pinout_fourth)
        images.add(R.drawable.pinout_fifth)
        images.add(R.drawable.pinout_fifth)
        images.add(R.drawable.pinout_sixth)
        images.add(R.drawable.pinout_sixth)
        images.add(R.drawable.pinout_seventh)
        images.add(R.drawable.pinout_seventh)
        images.add(R.drawable.pinout_eighth)
        images.add(R.drawable.pinout_eighth)

        images.shuffle()

        val catViews = ArrayList<ImageView>()
        for (i in 1..16) {
            catViews.add( // вызываем конструктор для создания нового ImageView
                ImageView(applicationContext).apply {
                    setImageResource(R.drawable.squarecat)
                    layoutParams = params
                    tag = images[i - 1] // TODO: указать тег в зависимости от картинки
                    setOnClickListener(colorListener)
                })
        }

        val rows = Array(4, { LinearLayout(applicationContext)})

        var count = 0
        for (view in catViews) {
            val row: Int = count / 4
            rows[row].addView(view)
            count ++
        }
        for (row in rows) {
            layout.addView(row)
        }
        setContentView(layout)

    }
    suspend fun setBackgroundWithDelay(v: View) {
//        delay(1000)
        val clickedCard = v as ImageView
        val imageRes = clickedCard.tag as Int
        clickedCard.setImageResource(imageRes)
        if (firstCard == null) {
            firstCard = clickedCard
        } else if (secondCard == null) {
            secondCard = clickedCard
            Log.i("cards: ", "${firstCard!!.tag} + ${secondCard!!.tag}")
            isClickable = false
            delay(900)
            checkPair()
        }
//        v.setBackgroundColor(Color.YELLOW)
//        delay(1000)
//        v.visibility = View.INVISIBLE
//        v.isClickable = false
    }

    private fun checkPair() {
        if (firstCard?.tag == secondCard?.tag) {
            firstCard?.visibility = View.INVISIBLE
            secondCard?.visibility = View.INVISIBLE
            openedPairs.add(firstCard!!)
            openedPairs.add(secondCard!!)
        }

        else {
            firstCard?.setImageResource(R.drawable.squarecat)
            secondCard?.setImageResource(R.drawable.squarecat)
        }

        firstCard = null
        secondCard = null
        isClickable = true

        if (openedPairs.size == images.size) {
            Toast.makeText(this, "ПОБЕДА!!!", Toast.LENGTH_SHORT).show()
        }
    }

    // обработчик нажатия на кнопку
    val colorListener = View.OnClickListener() {
        // запуск функции в фоновом потоке
        GlobalScope.launch (Dispatchers.Main)
        { setBackgroundWithDelay(it) }
    }

}