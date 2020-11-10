package com.nhlstenden.smarthome.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nhlstenden.smarthome.R

const val VIBRATE = "android.permission.VIBRATE"

/**
 * Util for easily creating and displaying notifications
 *
 * @author Robert
 * @since 1.0
 */
class Notification(
    private val context: Context,
    private val channelName: String,
    importance: Int = NotificationManager.IMPORTANCE_DEFAULT
) {
    private val notificationManagerCompat = NotificationManagerCompat.from(context)
    private var id = 0

    init {
        val channel = NotificationChannel(channelName, channelName, importance).apply {
            description = "Channel description"
        }

        // Create channel
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /** Shows a notification with the requested params, requires the [VIBRATE] permission to vibrate on notify */
    @RequiresPermission(VIBRATE)
    fun notify(
        title: String,
        text: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        category: String,
        vibrationPattern: LongArray = longArrayOf()
    ) {
        val id = id++
        val notification =
            NotificationCompat.Builder(context, channelName).setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(text).setContentTitle(title).setPriority(priority).setCategory(category)
                .setVibrate(vibrationPattern).build()

        notificationManagerCompat.notify(id, notification)
    }
}