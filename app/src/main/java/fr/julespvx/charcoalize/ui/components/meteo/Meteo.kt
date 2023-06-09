package fr.julespvx.charcoalize.ui.components.meteo

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import fr.julespvx.charcoalize.R
import fr.julespvx.charcoalize.data.meteo.Meteo
import fr.julespvx.charcoalize.data.meteo.MeteoApi
import kotlin.random.Random

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Meteo(
    modifier: Modifier = Modifier,
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
            MeteoApi.updateMeteo(
                context = context,
                locationPermissions = locationPermissionsState,
            )
        }
    }

    WeatherPreview(
        modifier = modifier,
        meteo = MeteoApi.meteo,
    )
}

@Preview
@Composable
fun WeatherPreview(
    modifier: Modifier = Modifier,
    meteo: Meteo = Meteo(),
) {
    Box(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.Center,
    ) {
        // Sun
        when (meteo.cloudiness.value) {
            in 0..25 -> {
                Sun()
            }
            in 26..50 -> {
                Cloudy(
                    darkness = 0.9f,
                )
            }
            in 51..75 -> {
                Cloudy(
                    darkness = 0.7f,
                    clouds = 2,
                )
            }
            in 76..100 -> {
                Cloudy(
                    darkness = 0.4f,
                    clouds = 2,
                )
            }
        }
        
    }
}

@Preview(group = "Elements")
@Composable
private fun Sun(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = R.drawable.sun),
        contentDescription = "Sun",
        modifier = Modifier
            .fillMaxHeight(0.6f),
    )
}

@Preview(group = "Elements")
@Composable
private fun Cloudy(
    modifier: Modifier = Modifier,
    darkness: Float = 0.5f,
    clouds: Int = 1,
) {
    val resource = when ((1..2).random()) {
        1 -> R.drawable.cloud_1
        2 -> R.drawable.cloud_2
        else -> R.drawable.cloud_1
    }
    when (clouds) {
        1 -> {
            Image(
                painter = painterResource(id = resource),
                contentDescription = "Cloudy",
                modifier = Modifier
                    .fillMaxHeight(0.6f),
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix().apply {
                        setToScale(darkness, darkness, darkness, 1f)
                    }
                ),
            )
        }
        2 -> {
            val cloudRandomDarkness = ((darkness * 100 - 7).toInt()..(darkness * 100 - 2).toInt()).random() / 100f
            Image(
                painter = painterResource(id = resource),
                contentDescription = "Cloudy",
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .padding(start = 50.dp),
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix().apply {
                        setToScale(cloudRandomDarkness, cloudRandomDarkness, cloudRandomDarkness, 1f)
                    }
                ),
            )
            Image(
                painter = painterResource(id = resource),
                contentDescription = "Cloudy",
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .padding(end = 20.dp, top = 20.dp),
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix().apply {
                        setToScale(darkness, darkness, darkness, 1f)
                    }
                ),
            )
        }
    }
}