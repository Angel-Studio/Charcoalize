package fr.julespvx.charcoalize.services.media

import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bumptech.glide.Glide.init

class MediaCallback(
    private val mediaController: MediaController,
    val onDestroyed: () -> Unit
) : MediaController.Callback() {

    private var playbackState: MutableState<PlaybackState?> = mutableStateOf(null)
    private var mediaMetadata: MediaMetadata? = null // TODO: Find why it cause recomposition every time

    var isPlaying: Boolean
        get() = playbackState.value?.state == PlaybackState.STATE_PLAYING
        set(value) {
            if (value) {
                mediaController.transportControls.play()
            } else {
                mediaController.transportControls.pause()
            }
        }

    val title: String
        get() = mediaMetadata?.getText(MediaMetadata.METADATA_KEY_TITLE) as String? ?: ""

    val artist: String
        get() = mediaMetadata?.getText(MediaMetadata.METADATA_KEY_ARTIST) as String? ?: ""

    val album: String
        get() = mediaMetadata?.getText(MediaMetadata.METADATA_KEY_ALBUM) as String? ?: ""

    val duration: Long
        get() = mediaMetadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0

    val cover: Bitmap
        get() = mediaMetadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART) ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    init {
        if (mediaController.metadata != null && mediaController.playbackState != null) {
            // Set the initial values for the media controller
            playbackState.value = mediaController.playbackState!!
            mediaMetadata = mediaController.metadata!!
            Log.d("MediaCallback", "Initialized media controller")
        }
    }

    fun skipToNext() { mediaController.transportControls.skipToNext() }
    fun skipToPrevious() { mediaController.transportControls.skipToPrevious() }

    override fun onPlaybackStateChanged(state: PlaybackState?) {
        super.onPlaybackStateChanged(state)
        playbackState.value = state ?: return
        Log.d("MediaCallback", "Playback state changed to ${state.state}")
    }

    override fun onMetadataChanged(metadata: MediaMetadata?) {
        super.onMetadataChanged(metadata)
        mediaMetadata = metadata ?: return
        Log.d("MediaCallback", "Metadata changed")
    }

    override fun onSessionDestroyed() {
        super.onSessionDestroyed()
        onDestroyed()
        Log.d("MediaCallback", "Session destroyed")
    }
}
