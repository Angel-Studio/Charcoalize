package fr.julespvx.charcoalize.data.air_quality

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.location.LocationServices
import fr.julespvx.charcoalize.data.VolleyRequestQueue

class AirQualityApi {
    companion object {
        var airQuality = AirQuality()

        @SuppressLint("MissingPermission") // Permission is checked before calling this function
        @OptIn(ExperimentalPermissionsApi::class)
        fun updateAirQuality(
            context: Context,
            locationPermissions: MultiplePermissionsState,
        ) {
            if (!locationPermissions.allPermissionsGranted) {
                Log.d("AirQualityApi", "Location permissions not granted")
                return
            }

            // Get location lat and long
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener {
                val lat = it.latitude
                val long = it.longitude

                val url = "https://api.waqi.info/feed/".plus(
                    "geo:$lat;$long/?token=80c77d1e7a2d1816eb6b825d32085fe9db5043ee"
                )
                Log.d("AirQualityApi", "updateAirQuality: $url")

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    { response ->
                        airQuality.aqi.value = response.getJSONObject("data").getDouble("aqi").toInt()
                        airQuality.percentage.value = 100 - response.getJSONObject("data").getDouble("aqi").toFloat() * 100 / 500 // Convert to percentage
                        airQuality.city.value = response.getJSONObject("data").getJSONObject("city").getString("name").substringBefore(",")
                        Log.d("AirQualityApi", "Air quality updated to $airQuality")
                    },
                    { error ->
                        Log.e("MoonPhaseApi", "updateMoonPhase: $error")
                    }
                )

                // Access the RequestQueue through your singleton class.
                VolleyRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest)
            }
        }
    }
}