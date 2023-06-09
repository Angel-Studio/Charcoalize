package fr.julespvx.charcoalize.ui.components

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import fr.julespvx.charcoalize.R
import fr.julespvx.charcoalize.data.moon.MoonPhase
import fr.julespvx.charcoalize.data.moon.MoonPhaseApi
import fr.julespvx.charcoalize.data.moon.Phases

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun MoonPhase(
    modifier: Modifier = Modifier,
    phases: Phases = Phases(),
) {
    val context = LocalContext.current

    // Location
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
    // Update moon phases or request permissions
    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        } else {
            MoonPhaseApi.updateMoonPhase(
                context = context,
                locationPermissions = locationPermissionsState,
            )
        }
    }

    // Moon phases animation
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.moon_phases))

    // Moon phase values
    val phase = if (phases.moonPhases.isEmpty()) MoonPhase() else phases.moonPhases[0]
    val progress = phase.phase.toFloat()
    val rotation = phase.tilt.toFloat() + 180f
    Log.d("MoonPhase", "Rotation: $rotation • Tilt: ${phase.tilt}")

    Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
        LottieAnimation(
            modifier = modifier
                .rotate(rotation)
                .blur(10.dp)
                .alpha(phase.illumination.toFloat()),
            composition = composition,
            progress = { progress },
        )
        LottieAnimation(
            modifier = modifier.rotate(rotation),
            composition = composition,
            progress = { progress },
        )
    }
}