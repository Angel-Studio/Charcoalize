package fr.julespvx.charcoalize

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import fr.julespvx.charcoalize.data.PersistentData
import fr.julespvx.charcoalize.navigation.CharcoalizeDestination
import fr.julespvx.charcoalize.navigation.CharcoalizeHome
import fr.julespvx.charcoalize.navigation.CharcoalizeNavHost
import fr.julespvx.charcoalize.navigation.mainDestinations
import fr.julespvx.charcoalize.navigation.navigateSingleTopTo
import fr.julespvx.charcoalize.services.BatteryReceiver
import fr.julespvx.charcoalize.services.media.MediaReceiver
import fr.julespvx.charcoalize.ui.components.WavesLoadingIndicator
import fr.julespvx.charcoalize.ui.theme.CharcoalizeTheme
import java.lang.reflect.Method
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign


class MainActivity : ComponentActivity() {
    @SuppressLint("RestrictedApi")
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Battery
        BatteryReceiver.register(this)
        // Media
        MediaReceiver.register(this)
        // Apps
        PersistentData.loadApps(this)

        setContent {
            val context = LocalContext.current
            val haptic = LocalHapticFeedback.current

            // Drag
            var verticalOffset by remember { mutableStateOf(0f) }
            var horizontalOffset by remember { mutableStateOf(0f) }
            val minDelta = 50

            // Search bar activation
            val searchBarActivated = rememberSaveable { mutableStateOf(false) }

            // Navigation
            val navController = rememberAnimatedNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStack?.destination
            val currentScreen : CharcoalizeDestination =
                mainDestinations.find { it.route == currentDestination?.route } ?: CharcoalizeHome

            CharcoalizeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDrag = { _, dragAmount -> verticalOffset += dragAmount.y },
                                        onDragEnd = {
                                            if (verticalOffset > minDelta) { // Swipe down
                                                when (currentScreen) {
                                                    is CharcoalizeHome -> {
                                                        setExpandNotificationDrawer(context, true)
                                                    }
                                                }

                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            } else if (verticalOffset < -minDelta) { // Swipe up
                                                when (currentScreen) {
                                                    is CharcoalizeHome -> {
                                                        searchBarActivated.value = true
                                                    }
                                                }

                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            }
                                            verticalOffset = 0f
                                        },
                                    )
                                    detectHorizontalDragGestures(
                                        onHorizontalDrag = { _, dragAmount -> verticalOffset += dragAmount },
                                    )
                                },
                        ) {
                            WavesLoadingIndicator(
                                modifier = Modifier
                                    .alpha(0.05f)
                                    .fillMaxSize(),
                                color = MaterialTheme.colorScheme.primary,
                                progress = animateFloatAsState(
                                    targetValue = if (BatteryReceiver.isCharging) BatteryReceiver.batteryPercent / 100f else 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessVeryLow,
                                    )
                                ).value,
                            )
                            CharcoalizeNavHost(
                                navController = navController,
                                innerPadding = innerPadding,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset(
                                        y = animateFloatAsState(
                                            targetValue = verticalOffset.absoluteValue.pow(0.5f) * 2 * verticalOffset.sign,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMediumLow
                                            )
                                        ).value.dp
                                    ),
                                searchBarActivated = searchBarActivated,
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("WrongConstant", "PrivateApi")
    fun setExpandNotificationDrawer(context: Context, expand: Boolean) {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val methodName =
                if (expand)
                    "expandNotificationsPanel"
                else
                    "collapsePanels"
            val statusBarManager: Class<*> = Class.forName("android.app.StatusBarManager")
            val method: Method = statusBarManager.getMethod(methodName)
            method.invoke(statusBarService)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}