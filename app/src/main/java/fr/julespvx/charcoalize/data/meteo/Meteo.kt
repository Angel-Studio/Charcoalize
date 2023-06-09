package fr.julespvx.charcoalize.data.meteo

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class Meteo(
    var temperatureC: MutableState<Float> = mutableStateOf(0f),
    var temperatureF: MutableState<Float> = mutableStateOf(0f),
    var temperatureFeelsLikeC: MutableState<Float> = mutableStateOf(0f),
    var temperatureFeelsLikeF: MutableState<Float> = mutableStateOf(0f),

    var windSpeedKmh: MutableState<Float> = mutableStateOf(0f),
    var windSpeedMph: MutableState<Float> = mutableStateOf(0f),

    var windDegree: MutableState<Int> = mutableStateOf(0),
    var windDirection: MutableState<String> = mutableStateOf(""),
    var gustKmh: MutableState<Float> = mutableStateOf(0f),
    var gustMph: MutableState<Float> = mutableStateOf(0f),

    var pressureMb: MutableState<Float> = mutableStateOf(0f),
    var pressureIn: MutableState<Float> = mutableStateOf(0f),

    var humidity: MutableState<Int> = mutableStateOf(0),
    var cloudiness: MutableState<Int> = mutableStateOf(0),
    var uvIndex: MutableState<Int> = mutableStateOf(0),

    var precipitationMm: MutableState<Float> = mutableStateOf(0f),
    var precipitationIn: MutableState<Float> = mutableStateOf(0f),
) {
    var isDay: MutableState<Boolean> = mutableStateOf(true)
}