package dev.sdex.aura.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dev.sdex.aura.R
import dev.sdex.aura.data.DataStore
import dev.sdex.aura.time.DateTimeFormatter
import timber.log.Timber

object NotificationHelper {

    fun showNotification(
        context: Context,
    ) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Timber.e("Notification permission is not granted")
            return
        }

        val channelId = "boot_info_channel"
        val description = context.getString(R.string.notification_description)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val deleteIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, NotificationDeleteReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.notification_description))
            .setContentText(getContentText(context))
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setDeleteIntent(deleteIntent)
        notificationManager.notify(1, builder.build())

        DataStore.onNotificationShown()
    }

    private fun getContentText(context: Context): String {
        val bootEvents = DataStore.getBootEvents()
        return when (bootEvents.size) {
            0 -> context.getString(R.string.no_boots_detected)
            1 -> context.getString(R.string.the_boot_was_detected, formatTime(bootEvents.first()))
            else -> context.getString(
                R.string.last_boots_time_delta,
                getEventsTimeDelta(bootEvents)
            )
        }
    }

    private fun formatTime(time: Long): String {
        val dateTimeFormatter = DateTimeFormatter()
        return dateTimeFormatter.format(time, DateTimeFormatter.NOTIFICATION_TIME_FORMAT)
    }

    private fun getEventsTimeDelta(bootEvents: List<Long>): String {
        val delta = bootEvents[bootEvents.size - 1] - bootEvents[bootEvents.size - 2]
        val dateTimeFormatter = DateTimeFormatter()
        return dateTimeFormatter.formatDuration(delta)
    }
}