package fr.julespvx.charcoalize.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class BatteryReceiver {

    companion object {

        var batteryPercent by mutableStateOf(0)
        var isCharging by mutableStateOf(false)

        private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // Get battery status extra
                val status = intent.extras!!.getInt(BatteryManager.EXTRA_STATUS)
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING

                // If charging, add plugin else remove it
                if (isCharging) {
                    val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val maxBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    // Set battery percent
                    batteryPercent = (batteryLevel * 100 / maxBatteryLevel)
                    Log.d("BatteryListener", "Battery percent: $batteryPercent")
                }
            }
        }

        fun register(context: Context) {
            Log.d("BatteryListener", "Registering battery listener")
            context.registerReceiver(mBroadcastReceiver, IntentFilter(ACTION_BATTERY_CHANGED))
        }

        fun unregister(context: Context) {
            Log.d("BatteryListener", "Unregistering battery listener")
            context.unregisterReceiver(mBroadcastReceiver)
        }
    }
}