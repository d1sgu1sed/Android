package com.example.sensors

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.hardware.Sensor
import android.hardware.SensorManager
import android.content.Context
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var spinnerCategories: Spinner
    private lateinit var recyclerSensors: RecyclerView
    private lateinit var sensorsAdapter: SensorsAdapter

    private lateinit var allSensors: List<Sensor>

    private val environmentSensors = mutableListOf<Sensor>()
    private val positionSensors = mutableListOf<Sensor>()
    private val humanSensors = mutableListOf<Sensor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

        val environmentSensorTypes = listOf(
            Sensor.TYPE_AMBIENT_TEMPERATURE,
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_RELATIVE_HUMIDITY,
        )

        val positionSensorTypes = listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_GRAVITY,
            Sensor.TYPE_ROTATION_VECTOR,
            Sensor.TYPE_PROXIMITY
        )

        val humanSensorTypes = listOf(
            Sensor.TYPE_HEART_RATE,
            Sensor.TYPE_STEP_DETECTOR,
            Sensor.TYPE_STEP_COUNTER
        )

        allSensors.forEach { sensor ->
            when (sensor.type) {
                in environmentSensorTypes -> environmentSensors.add(sensor)
                in positionSensorTypes -> positionSensors.add(sensor)
                in humanSensorTypes -> humanSensors.add(sensor)
            }
        }

        spinnerCategories = findViewById(R.id.spinner_categories)
        recyclerSensors = findViewById(R.id.recycler_sensors)

        sensorsAdapter = SensorsAdapter(emptyList())
        recyclerSensors.layoutManager = LinearLayoutManager(this)
        recyclerSensors.adapter = sensorsAdapter

        val categoriesAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            SensorCategory.values()
        )
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategories.adapter = categoriesAdapter

        spinnerCategories.setOnItemSelectedListener { category ->
            // Фильтруем по выбранной категории
            val selectedCategory = spinnerCategories.selectedItem as SensorCategory
            val sensorsToShow = when (selectedCategory) {
                SensorCategory.ENVIRONMENT -> environmentSensors
                SensorCategory.POSITION -> positionSensors
                SensorCategory.HUMAN -> humanSensors
            }
            sensorsAdapter.updateData(sensorsToShow)
        }
    }

    private fun Spinner.setOnItemSelectedListener(listener: (Int) -> Unit) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                listener(position)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }
}