package dev.sdex.aura.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sdex.aura.data.DataStore
import timber.log.Timber
import java.util.concurrent.TimeUnit

class NotificationDeleteReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Notification delete intent received, schedule new notification")
        DataStore.onNotificationDismissed()
        val totalDismissals = DataStore.getTotalDismissals()
        // If the total number of dismissals exceeds the limit,
        // notification scheduling should revert to the 15-minute rule
        val interval = if (totalDismissals >= DataStore.getTotalAllowedDismissals()) {
            NotificationScheduler.DEFAULT_INTERVAL
        } else {
            TimeUnit.MINUTES.toMillis(totalDismissals * DataStore.getIntervalBetweenDismissals())
        }
        Timber.d("Notification interval: $interval")
        val notificationScheduler = NotificationScheduler()
        notificationScheduler.scheduleNotification(context, interval)
    }
}