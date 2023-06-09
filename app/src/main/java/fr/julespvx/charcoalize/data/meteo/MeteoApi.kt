package fr.julespvx.charcoalize.data.meteo

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.location.LocationServices
import fr.julespvx.charcoalize.data.VolleyRequestQueue

class MeteoApi {
    companion object {
        var meteo = Meteo()

        @SuppressLint("MissingPermission") // Permission is checked before calling this function
        @OptIn(ExperimentalPermissionsApi::class)
        fun updateMeteo(
            context: Context,
            locationPermissions: MultiplePermissionsState,
        ) {
            if (!locationPermissions.allPermissionsGranted) {
                Log.d("MeteoApi", "updateMeteo: Location permission not granted")
                return
            }

            // Get location lat and long
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener {
                val lat = it.latitude
                val long = it.longitude

                val url = "https://api.weatherapi.com/v1/current.json?key=346c4475e1f3483199793418233105&q=\n".plus(
                    "$lat,$long&aqi=no"
                )
                Log.d("MeteoApi", "updateMeteo: $url")

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    { response ->
                        val current = response.getJSONObject("current")
                        meteo.cloudiness.value = current.getInt("cloud")
                        meteo.temperatureC.value = current.getDouble("temp_c").toFloat()
                        meteo.temperatureF.value = current.getDouble("temp_f").toFloat()
                        meteo.temperatureFeelsLikeC.value = current.getDouble("feelslike_c").toFloat()
                        meteo.temperatureFeelsLikeF.value = current.getDouble("feelslike_f").toFloat()
                        meteo.humidity.value = current.getDouble("humidity").toInt()
                        meteo.windSpeedKmh.value = current.getDouble("wind_kph").toFloat()
                        meteo.windSpeedMph.value = current.getDouble("wind_mph").toFloat()
                        meteo.windDegree.value = current.getInt("wind_degree")
                        meteo.windDirection.value = current.getString("wind_dir")
                        meteo.pressureMb.value = current.getDouble("pressure_mb").toFloat()
                        meteo.pressureIn.value = current.getDouble("pressure_in").toFloat()
                        meteo.precipitationMm.value = current.getDouble("precip_mm").toFloat()
                        meteo.precipitationIn.value = current.getDouble("precip_in").toFloat()
                        meteo.uvIndex.value = current.getInt("uv")
                        meteo.gustKmh.value = current.getDouble("gust_kph").toFloat()
                        meteo.gustMph.value = current.getDouble("gust_mph").toFloat()
                        meteo.isDay.value = current.getInt("is_day") == 1
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