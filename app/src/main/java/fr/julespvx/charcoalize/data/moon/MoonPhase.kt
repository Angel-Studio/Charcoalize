package fr.julespvx.charcoalize.data.moon

import java.util.Calendar

data class MoonPhase(
    val age: Double = 0.0,
    val date: Calendar = Calendar.getInstance(),
    val diameter: Double = 0.0,
    val illumination: Double = 0.0,
    val distance: Double = 0.0,
    val moonSign: String = "",
    val phase: Double = 0.0,
    val phaseImg: String = "",
    val phaseName: String = "",
    val stage: String = "",
    val tilt: Double = 0.0,
    val moonrise: Calendar = Calendar.getInstance(),
    val moonset: Calendar = Calendar.getInstance(),
)

data class Phases(
    val nextFull: Calendar = Calendar.getInstance(),
    val nextMoonrise: Calendar = Calendar.getInstance(),
    val nextMoonset: Calendar = Calendar.getInstance(),
    val nextNew: Calendar = Calendar.getInstance(),
    val moonPhases: List<MoonPhase> = listOf(),
)