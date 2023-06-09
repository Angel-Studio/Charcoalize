package fr.julespvx.charcoalize.services.media

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import fr.julespvx.charcoalize.services.NotificationService

class MediaReceiver {

    companion object {
        // Map of package name to callback
        private val callbackMap = mutableStateMapOf<String, MediaCallback>()
        private lateinit var mediaSessionManager: MediaSessionManager

        val firstCallback: MediaCallback?
            get() = callbackMap.values.firstOrNull()

        private val listenerForActiveSessions =
            MediaSessionManager.OnActiveSessionsChangedListener { mediaControllers ->
                if (mediaControllers != null) {
                    for (mediaController in mediaControllers) {
                        // Cancel if already exists
                        if (callbackMap[mediaController.packageName] != null) continue

                        // Create callback for this media controller and add it to the map of callbacks
                        val callback = MediaCallback(mediaController) { removeMedia(mediaController) }
                        callbackMap[mediaController.packageName] = callback
                        mediaController.registerCallback(callback)
                    }
                }
            }

        private fun removeMedia(mediaController: MediaController) {
            callbackMap.remove(mediaController.packageName)
        }

        fun register(context: Context) {
            Log.d("MediaReceiver", "Registering media receiver")

            // Get the media session manager
            if (!::mediaSessionManager.isInitialized) mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager

            // Register the listener for active sessions (new sessions)
            mediaSessionManager.addOnActiveSessionsChangedListener(listenerForActiveSessions, ComponentName(context, NotificationService::class.java))
            Log.d("MediaReceiver", "Registered listener for active sessions")
            // Register callbacks for already active sessions (if any)
            mediaSessionManager.getActiveSessions(ComponentName(context, NotificationService::class.java)).forEach { mediaController ->
                // Cancel if already exists
                if (callbackMap[mediaController.packageName] != null) return@forEach

                Log.d("MediaReceiver", "Registering callback for already active session ${mediaController.packageName}")
                // Create callback for this media controller and add it to the map of callbacks
                val mediaCallback = MediaCallback(mediaController) { removeMedia(mediaController) }
                callbackMap[mediaController.packageName] = mediaCallback
                // Register callback
                mediaController.registerCallback(mediaCallback)
            }
            Log.d("MediaReceiver", "Registered callbacks for already active sessions")

            Log.d("MediaReceiver", "Callback map: $callbackMap")
        }
    }

}