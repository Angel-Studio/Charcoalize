package fr.julespvx.charcoalize.data.moon

import android.annotation.SuppressLint
import android.content.Context
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MoonPhaseApi {
    companion object {
        var moonPhases by mutableStateOf(Phases())

        @SuppressLint("MissingPermission") // Permission is checked before calling this function
        @OptIn(ExperimentalPermissionsApi::class)
        fun updateMoonPhase(
            context: Context,
            locationPermissions: MultiplePermissionsState,
        ) {
            if (!locationPermissions.allPermissionsGranted) {
                Log.d("MoonPhaseApi", "Location permissions not granted")
                return
            }

            val date = Calendar.getInstance()
            val day = date.get(Calendar.DAY_OF_MONTH)
            val month = date.get(Calendar.MONTH) + 1
            val year = date.get(Calendar.YEAR)

            // get location lat and long
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener {
                val lat = it.latitude
                val long = it.longitude

                val url = "https://moonphases.co.uk/service/getMoonDetails.php?".plus(
                    "&day=$day&month=$month&year=$year&lat=$lat&lng=$long&len=1&tz=0"
                )
                Log.d("MoonPhaseApi", "Getting moon phases from $url")

                val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                    { response ->
                        val phases = mutableListOf<MoonPhase>()
                        val days = response.getJSONArray("days")
                        // Get the next 6 days of moon phases
                        for (i in 0 until days.length()) {
                            val phase = days.getJSONObject(i)
                            val moonPhase = MoonPhase(
                                age = phase.getDouble("age"),
                                date = Calendar.getInstance().apply { timeInMillis = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(phase.getString("date_serial"))?.time ?: 0 },
                                diameter = phase.getDouble("diameter"),
                                illumination = phase.getDouble("illumination"),
                                distance = phase.getDouble("distance"),
                                moonSign = phase.getString("moonsign"),
                                phase = phase.getDouble("phase"),
                                phaseImg = phase.getString("phase_img"),
                                phaseName = phase.getString("phase_name"),
                                stage = phase.getString("stage"),
                                tilt = phase.getDouble("tilt"),
                                moonrise = Calendar.getInstance().apply { timeInMillis = SimpleDateFormat("EEE MMM d yyyy H:mm", Locale.getDefault()).parse(phase.getString("moonrise"))?.time ?: 0 },
                                moonset = Calendar.getInstance().apply { timeInMillis = SimpleDateFormat("EEE MMM d yyyy H:mm", Locale.getDefault()).parse(phase.getString("moonset"))?.time ?: 0 },
                            )
                            phases.add(moonPhase)
                        }

                        moonPhases = Phases(
                            nextFull = Calendar.getInstance().apply { timeInMillis = SimpleDateFormat("EEE MMM d yyyy H:mm", Locale.getDefault()).parse(response.getString("next_full"))?.time ?: 0 },
                            nextMoonrise = Calendar.getInstance().apply { timeInMillis = SimpleDateFormat("EEE MMM d yyyy H:mm", Locale.getDefault()).parse(response.getString("next_moonrise"))?.time ?: 0 },
                            nextMoonset = Calendar.getInstance().apply { timeInMillis = SimpleDateFormat("EEE MMM d yyyy H:mm", Locale.getDefault()).parse(response.getString("next_moonset"))?.time ?: 0 },
                            nextNew = Calendar.getInstance().apply { timeInMillis = SimpleDateFormat("EEE MMM d yyyy H:mm", Locale.getDefault()).parse(response.getString("next_new"))?.time ?: 0 },
                            moonPhases = phases
                        )
                        Log.d("MoonPhaseApi", "Moon phases updated")
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