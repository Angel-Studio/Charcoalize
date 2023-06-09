package fr.julespvx.charcoalize.data

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.lang.Thread.sleep

class Time {
    companion object {
        var time = mutableStateOf(System.currentTimeMillis())

        init {
            val mainHandler = Handler(Looper.getMainLooper())

            mainHandler.post(object : Runnable {
                override fun run() {
                    time.value = System.currentTimeMillis()
                    mainHandler.postDelayed(this, 1000)
                }
            })
        }
    }
}