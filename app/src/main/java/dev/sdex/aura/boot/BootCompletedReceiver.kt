package dev.sdex.aura.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sdex.aura.data.DataStore
import dev.sdex.aura.notification.NotificationHelper
import dev.sdex.aura.notification.NotificationScheduler
import timber.log.Timber

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Timber.d("Boot completed intent received")
            DataStore.addBootEvent()
            if (DataStore.shouldRestoreNotification()) {
                Timber.i("Notification should be restored")
                NotificationHelper.showNotification(context)
            } else {
                Timber.i("Notification should not be restored, scheduling new notification")
                val notificationScheduler = NotificationScheduler()
                notificationScheduler.scheduleNotification(context)
            }
        }
    }
}