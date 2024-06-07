package dev.sdex.aura.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sdex.aura.notification.NotificationHelper
import timber.log.Timber

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Alarm intent received, showing notification")
        NotificationHelper.showNotification(context)
    }
}