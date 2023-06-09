package fr.julespvx.charcoalize.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.coroutineScope
import java.util.Locale
import kotlin.math.absoluteValue

@SuppressLint("MissingPermission") // Location permission is checked
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    innerPadding: PaddingValues = PaddingValues(),
    searchBarActivated: MutableState<Boolean>,
) {
    val context = LocalContext.current

    val pagerState = rememberPagerState(initialPage = 0)
    HorizontalPager(
        pageCount = 2,
        state = pagerState,
        modifier = Modifier
            .fillMaxSize(),
        flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            lowVelocityAnimationSpec = spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy
            )
        ),
        userScrollEnabled = !searchBarActivated.value,
    ) { page ->
        val modifier = Modifier
            .graphicsLayer {
                val pageOffset = (
                        (pagerState.currentPage - page) + pagerState
                            .currentPageOffsetFraction
                        ).absoluteValue
                alpha = lerp(
                    start = 0.5f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
            }.fillMaxSize()
        when (page) {
            0 -> { HomePageScreen(
                modifier = Modifier
                    .fillMaxSize(),
                searchBarActivated = searchBarActivated,
            ) }
            1 -> { WidgetsPageScreen(
                modifier = Modifier
                    .fillMaxSize(),
                innerPadding = innerPadding,
            ) }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun getLocationNameApi33(
    latitude: Double,
    longitude: Double,
    context: Context
): String {
    var cityName = "Not Found"
    val geocoder = Geocoder(context, Locale.getDefault())
    val maxResult = 1

    //Fetch address from location
    geocoder.getFromLocation(latitude, longitude, maxResult, object : Geocoder.GeocodeListener {
        override fun onGeocode(addresses: MutableList<Address>) {
            if (addresses.size > 0) {
                cityName = addresses[0].locality
            }
        }
        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            Log.d("TAG", "Error receiving location: $errorMessage")
        }
    })
    return cityName
}

suspend fun getLocationName(
    latitude: Double,
    longitude: Double,
    context: Context,
): String = coroutineScope {
    var cityName = "Not Found"
    val geocoder = Geocoder(context, Locale.getDefault())
    val maxResult = 1

    //Fetch address from location
    val addresses = geocoder.getFromLocation(latitude, longitude, maxResult) as MutableList<Address>
    if (addresses.size > 0) {
        cityName = addresses[0].locality
    }
    return@coroutineScope cityName
}