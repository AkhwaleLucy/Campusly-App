package com.example.gossipapp.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.gossipapp.R
import java.util.Calendar

class CampuslyNotificationManager(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "campusly_notifications"
        private const val CHANNEL_NAME = "Campusly Academic & Community Alerts"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for class schedules, assignments, and campus forum alerts"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun isQuietHoursActive(startTime: String, endTime: String, now: Calendar = Calendar.getInstance()): Boolean {
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val startMinutes = parseTimeToMinutes(startTime)
        val endMinutes = parseTimeToMinutes(endTime)

        return if (startMinutes > endMinutes) {
            // Overnight quiet hours range (e.g. 22:00 PM to 07:00 AM)
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        } else {
            // Daytime quiet hours range (e.g. 13:00 to 15:00)
            currentMinutes in startMinutes until endMinutes
        }
    }

    private fun parseTimeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        if (parts.size < 2) return 0
        val h = parts[0].trim().toIntOrNull() ?: 0
        val m = parts[1].trim().toIntOrNull() ?: 0
        return h * 60 + m
    }

    fun sendNotification(
        title: String,
        body: String,
        isQuietHoursEnabled: Boolean,
        quietHoursStart: String,
        quietHoursEnd: String
    ) {
        if (isQuietHoursEnabled && isQuietHoursActive(quietHoursStart, quietHoursEnd)) {
            Toast.makeText(context, "🔕 Notification suppressed during Quiet Hours ($quietHoursStart - $quietHoursEnd)", Toast.LENGTH_SHORT).show()
            return
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }
}
