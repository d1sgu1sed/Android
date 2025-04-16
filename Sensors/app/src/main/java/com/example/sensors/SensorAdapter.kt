package com.example.sensors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.hardware.Sensor

class SensorsAdapter(private var sensors: List<Sensor>) :
    RecyclerView.Adapter<SensorsAdapter.SensorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return SensorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        val sensor = sensors[position]
        holder.bind(sensor)
    }

    override fun getItemCount(): Int = sensors.size

    fun updateData(newSensors: List<Sensor>) {
        sensors = newSensors
        notifyDataSetChanged()
    }

    inner class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textSensorName: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(sensor: Sensor) {
            textSensorName.text = sensor.name
        }
    }
}
