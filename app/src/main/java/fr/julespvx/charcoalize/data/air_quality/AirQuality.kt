package fr.julespvx.charcoalize.data.air_quality

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

data class AirQuality(
    var aqi: MutableState<Int> = mutableStateOf(0),
    var percentage: MutableState<Float> = mutableStateOf(0f),
    var city: MutableState<String> = mutableStateOf(""),

    val status: AirQualityStatus = getAirQualityStatus(aqi.value)
)

enum class AirQualityStatus(
    val color: Color,
    val label: String,
) {
    GOOD(Color(0xFFA5D6A7), "Good"),
    MODERATE(Color(0xFFC5E1A5), "Moderate"),
    UNHEALTHY_FOR_SENSITIVE_GROUPS(Color(0xFFFFF59D), "Unhealthy for sensitive groups"),
    UNHEALTHY(Color(0xFFFFCC80), "Unhealthy"),
    VERY_UNHEALTHY(Color(0xFFEF9A9A), "Very unhealthy"),
    HAZARDOUS(Color(0xFFCE93D8), "Hazardous"),
}

private fun getAirQualityStatus(aqi: Int): AirQualityStatus {
    return when (aqi) {
        in 0..50 -> AirQualityStatus.GOOD
        in 51..100 -> AirQualityStatus.MODERATE
        in 101..150 -> AirQualityStatus.UNHEALTHY_FOR_SENSITIVE_GROUPS
        in 151..200 -> AirQualityStatus.UNHEALTHY
        in 201..300 -> AirQualityStatus.VERY_UNHEALTHY
        else -> AirQualityStatus.HAZARDOUS
    }
}