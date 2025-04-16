package com.example.geolocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.geolocation.R

class MainActivity : AppCompatActivity(), LocationListener {
    private val LOCATION_PERM_CODE = 2
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERM_CODE
            )
        } else {
            startLocationUpdates()
        }

        findViewById<Button>(R.id.updButton).setOnClickListener {
            val provider = locationManager.getBestProvider(Criteria(), true)
            if (provider != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null) {
                    displayCoord(location.latitude, location.longitude)
                } else {
                    displayCoordText("Unknown", "Unknown")
                }
            }
        }

        val allProviders = locationManager.allProviders
        val enabledProviders = locationManager.getProviders(true)
        findViewById<TextView>(R.id.providersText).text =
            "Available: $enabledProviders\nAll: $allProviders"
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                5f,
                this
            )
            val provider = locationManager.getBestProvider(Criteria(), true)
            if (provider != null) {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null) {
                    displayCoord(location.latitude, location.longitude)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERM_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startLocationUpdates()
            } else {
                displayCoordText("No permission", "No permission")
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        displayCoord(location.latitude, location.longitude)
        findViewById<TextView>(R.id.statusText).text = "Status: Online"
        Log.d("my", "lat: ${location.latitude}, long: ${location.longitude}")
    }

    override fun onProviderEnabled(provider: String) {
        findViewById<TextView>(R.id.statusText).text = "Status: Online"
    }

    override fun onProviderDisabled(provider: String) {
        findViewById<TextView>(R.id.statusText).text = "Status: Offline"
    }

    private fun displayCoord(latitude: Double, longitude: Double) {
        displayCoordText(String.format("%.5f", latitude), String.format("%.5f", longitude))
    }

    private fun displayCoordText(latText: String, lngText: String) {
        findViewById<TextView>(R.id.lat).text = latText
        findViewById<TextView>(R.id.lng).text = lngText
    }
}