package fr.julespvx.charcoalize.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.location.Location
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.skydoves.landscapist.rememberDrawablePainter
import fr.julespvx.charcoalize.data.App
import fr.julespvx.charcoalize.data.PersistentData
import fr.julespvx.charcoalize.data.air_quality.AirQualityApi
import fr.julespvx.charcoalize.data.meteo.MeteoApi
import fr.julespvx.charcoalize.data.moon.MoonPhaseApi
import fr.julespvx.charcoalize.services.media.MediaReceiver
import fr.julespvx.charcoalize.ui.components.ClockText
import fr.julespvx.charcoalize.ui.components.DateText
import fr.julespvx.charcoalize.ui.components.MoonPhase
import fr.julespvx.charcoalize.ui.components.meteo.Meteo
import fr.julespvx.charcoalize.ui.components.MusicPlayer

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class
)
@Composable
fun HomePageScreen(
    modifier: Modifier = Modifier,
    searchBarActivated: MutableState<Boolean>,
) {
    val context = LocalContext.current

    // Alarm
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    var nextAlarm: Long? = null
    if (alarmManager.nextAlarmClock != null) {
        nextAlarm = alarmManager.nextAlarmClock.triggerTime
    }
    val formattedNextAlarm = if (nextAlarm != null) {
        DateUtils.formatDateTime(
            context,
            nextAlarm,
            DateUtils.FORMAT_SHOW_TIME
        )
    } else {
        "None"
    }

    // Music
    val mediaCallback = MediaReceiver.firstCallback

    // Location
    var cityName by remember { mutableStateOf("") }
    val locationPermission = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    if (locationPermission.status.isGranted) {
        // Get location lat and long
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        var lastLocation by remember { mutableStateOf<Location?>(null) }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            lastLocation = it
            Log.d("Location", "Location: $it")
        }
        LaunchedEffect(lastLocation) {
            val lat = lastLocation?.latitude ?: 0.0
            val long = lastLocation?.longitude ?: 0.0
            cityName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getLocationNameApi33(lat, long, context)
            } else {
                getLocationName(lat, long, context)
            }
        }
    }

    // Air quality
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
    LaunchedEffect(Unit) {
        if (locationPermissionsState.allPermissionsGranted) {
            AirQualityApi.updateAirQuality(context, locationPermissionsState)
            MeteoApi.updateMeteo(context, locationPermissionsState)
        } else {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    // Search
    var query by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val appsQuery = PersistentData.apps
        .filter { it.label.contains(
            query.trim(),
            ignoreCase = true
        ) }.sortedBy {
            it.label.lowercase().indexOf(query.lowercase())
        }

    LaunchedEffect(searchBarActivated.value) {
        if (searchBarActivated.value) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        // Search bar
        Box(
            Modifier
                .semantics { isContainer = true }
                .zIndex(1f)
                .fillMaxWidth()) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .focusRequester(focusRequester),
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    context.startActivity(appsQuery.first().intent)
                },
                active = searchBarActivated.value,
                onActiveChange = {
                    searchBarActivated.value = it
                },
                placeholder = { Text("Search") },
                leadingIcon = {
                    AnimatedContent(
                        targetState = searchBarActivated.value,
                        transitionSpec = {
                            if (searchBarActivated.value) {
                                slideInVertically { height -> height } + fadeIn() with
                                        slideOutVertically { height -> -height } + fadeOut()
                            } else {
                                slideInVertically { height -> -height } + fadeIn() with
                                        slideOutVertically { height -> height } + fadeOut()
                            }.using(
                                SizeTransform(clip = false)
                            )
                        }
                    ) {
                        if (it) {
                            IconButton(onClick = {
                                searchBarActivated.value = false
                                query = ""
                            }) {
                                Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                            }
                        } else {
                            Icon(Icons.Rounded.Search, contentDescription = null)
                        }
                    }
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                    ) {
                        IconButton(onClick = {
                            query = ""
                        }) {
                            Icon(Icons.Rounded.Close, contentDescription = null)
                        }
                    }
                },
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(appsQuery, key = { it.packageName }) { appInfo ->
                        AppItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            app = appInfo,
                            onClick = {
                                context.startActivity(appInfo.intent,)
                            },
                            onShortcutClick = { shortcut ->
                                val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                                launcherApps.startShortcut(shortcut, null, null)
                            },
                        )
                    }
                }
            }
        }

        // Content
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.18f))
            // Clock and date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .zIndex(100f)
                ) {
                    DateText(
                        style = MaterialTheme.typography.labelLarge,
                    )
                    ClockText(
                        style = MaterialTheme.typography.displayLarge,
                    )
                    Text(
                        text = "Next alarm: $formattedNextAlarm",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = "Location: $cityName",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = "Air quality: ${AirQualityApi.airQuality.percentage.value}%",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                // Moon Phase
                if (MeteoApi.meteo.isDay.value) {
                    Meteo(
                        modifier = Modifier
                            .padding(end = 16.dp, start = 16.dp)
                            .height(IntrinsicSize.Max),
                    )
                } else {
                    MoonPhase(
                        modifier = Modifier
                            .padding(end = 16.dp, start = 16.dp)
                            .height(125.dp)
                            .alpha(0.75f),
                        phases = MoonPhaseApi.moonPhases
                    )
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
            if (mediaCallback != null) {
                // Music player
                MusicPlayer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.2f)
                        .padding(horizontal = 32.dp),
                    title = mediaCallback.title,
                    artist = mediaCallback.artist,
                    album = mediaCallback.album,
                    cover = mediaCallback.cover,
                    isPlaying = mediaCallback.isPlaying,
                    onPlayPause = {
                        mediaCallback.isPlaying = !mediaCallback.isPlaying
                    },
                    onSkipNext = { mediaCallback.skipToNext() },
                    onSkipPrevious = { mediaCallback.skipToPrevious() },
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AppItem(
    modifier: Modifier = Modifier,
    app: App,
    onClick: () -> Unit = {},
    onShortcutClick: (ShortcutInfo) -> Unit = {},
    state: Boolean = false,
) {
    var expanded by remember { mutableStateOf(state) }
    val packageManager = LocalContext.current.packageManager

    OutlinedCard(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = rememberDrawablePainter(app.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = app.label)
                Spacer(modifier = Modifier.weight(1f))
                if (app.shortcutInfo.isNotEmpty()) {
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier
                            .rotate(if (expanded) 180f else 0f)
                    ) {
                        Icon(Icons.Rounded.ExpandMore, contentDescription = null)
                    }
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    app.shortcutInfo.forEach { shortcutInfo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    onShortcutClick(shortcutInfo)
                                },
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Log.d("AppItem", "AppItem: ${shortcutInfo.extras}")
                                Image(
                                    painter = rememberDrawablePainter(drawable = packageManager.getActivityIcon(shortcutInfo.activity!!)),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = shortcutInfo.longLabel.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}