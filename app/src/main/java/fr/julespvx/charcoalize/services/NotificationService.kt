package fr.julespvx.charcoalize.services

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationService : NotificationListenerService() {

    companion object {

    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        super.onNotificationPosted(statusBarNotification)
        if (statusBarNotification == null) return

        val notification = statusBarNotification.notification

        // Ignore notifications from ->
        when (notification.category) {
            Notification.CATEGORY_SYSTEM, // Ignore system notifications
            Notification.CATEGORY_SERVICE, // Ignore service notifications
            Notification.CATEGORY_TRANSPORT, // Ignore media player controls notifications
            -> return
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationService", "Service created")
    }
}