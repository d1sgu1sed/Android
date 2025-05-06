package com.example.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import java.util.Scanner
import kotlin.io.path.Path

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailedWeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailedWeatherFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var cities = arrayOf("Irkutsk", "Moscow", "Krasnoyarsk", "Krasnodar")
    private var param1: String? = null
    private var param2: String? = null
    private var weather_text: Weather = Weather()
    private var lang: String = "ru"
    private var sc: Scanner? = null
    private var key: String = "c25f13190379f58bcfa4d76cdc9551c8"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater!!.inflate(R.layout.fragment_detailed_weather, container, false)
        val weatherInfoTextView = view.findViewById<TextView>(R.id.weather_info)
        val weatherImageView = view.findViewById<ImageView>(R.id.weather_image)
        val changeCityButton = view.findViewById<Button>(R.id.change_city_button)
        var choice = 0

        changeCityButton.setOnClickListener(View.OnClickListener {
            AlertDialog.Builder(view.context).
            setSingleChoiceItems( arrayOf("Irkutsk", "Moscow", "Krasnoyarsk", "Krasnodar"), 0, {
                    dialog, which -> choice = which
            }).
            setPositiveButton("Ok", {dialog, which ->
                set_weather_data(weatherInfoTextView, weatherImageView, cities[choice])
            }).
            create().show()
        })

        set_weather_data(weatherInfoTextView, weatherImageView, "Irkutsk")

        return view
    }

    suspend fun getWeather(key: String, lang: String, city: String): Weather {
        val API_URL = "https://api.openweathermap.org/data/2.5/weather?q=$city&lang=ru&appid=${key}&units=metric"

        val stream = URL(API_URL).getContent() as InputStream
        // JSON отдаётся одной строкой,
        val data = Scanner(stream).nextLine()
        Log.d("mytag", data)
        val gson = Gson()
        return gson.fromJson(data, Weather::class.java)
    }

    fun getWindDirection(degrees: Double): String {
        return when {
            degrees < 0 || degrees >= 360 -> "Некорректное значение"
            degrees < 22.5 || degrees >= 337.5 -> "Северный"
            degrees < 67.5 -> "Северо-восточный"
            degrees < 112.5 -> "Восточный"
            degrees < 157.5 -> "Юго-восточный"
            degrees < 202.5 -> "Южный"
            degrees < 247.5 -> "Юго-западный"
            degrees < 292.5 -> "Западный"
            degrees < 337.5 -> "Северо-западный"
            else -> "Некорректное значение"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun set_weather_data(weatherInfoTextView: TextView, weatherImageView: ImageView, city: String){
        lifecycleScope.launch {
            val weather = withContext(Dispatchers.IO) {
                getWeather(key, lang, city)
            }
            val fileName = "weather.json"
            val tenMinutesInMillis = 10 * 60 * 1000

            view?.context?.filesDir?.let { fileDir ->
                val filePath = Path(fileDir.path, fileName)
                val file = filePath.toFile()

                val shouldUpdate = !file.exists() || (Date().time - file.lastModified() > tenMinutesInMillis)

                if (shouldUpdate) {
                    try {
                        val gson = Gson() // Create Gson instance
                        val jsonString = gson.toJson(weather) // Convert Weather to JSON using Gson
                        FileOutputStream(file).use { output ->
                            output.write(jsonString.toByteArray())
                        }
                        Log.d("WeatherFileGson", "Weather data saved to file (Gson): ${file.absolutePath}")
                    } catch (e: Exception) {
                        Log.e("WeatherFileGson", "Error writing weather data to file (Gson): ${e.message}")
                        e.printStackTrace()
                    }
                } else {
                    Log.d("WeatherFileGson", "Weather data is recent enough, not updating file (Gson).")
                }

                Log.d("fileDir", "filepath ${fileDir}")
                Log.d("path_to_file", "filepath: $filePath")

            } ?: run {
                Log.e("WeatherFileGson", "Context or filesDir is null. Cannot save weather data (Gson).")
            }

            weatherInfoTextView.text = buildString {
                append("В $city сегодня ${weather.weather.get(0).description}\n")
                append("Температура: ${weather.main.temp}°C, ощущается как: ${weather.main.feels_like}°C\n")
                append("Ветер ${getWindDirection(weather.wind.deg.toDouble()).toLowerCase()}, ${weather.wind.speed} м/с")
            }
            val weather_type = weather.weather.get(0).main
            weatherImageView.setImageResource(when {
                weather_type.toLowerCase() == "cнег" -> R.drawable.snow
                weather_type.toLowerCase() == "гроза" -> R.drawable.thunder
                weather_type.toLowerCase() == "ветрено" -> R.drawable.windy
                weather_type.toLowerCase() == "дождь" -> R.drawable.rainy
                else -> R.drawable.clouds_and_sun
            })

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailedWeatherFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailedWeatherFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

//    fun change_city(view: View) {
//        val myDialog = MyDialog(view.context)
//        fragmentManager?.let { myDialog.show(it, "city_choice") }
//    }
//
//    override fun onClick(dialog: DialogInterface?, which: Int) {
//        val textView =
//        when (which){
//            DialogInterface.BUTTON_POSITIVE -> textView.setText("Вы согласились!");
//        }
//
//    }

}