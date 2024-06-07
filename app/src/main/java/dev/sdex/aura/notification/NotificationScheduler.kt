package dev.sdex.aura.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.sdex.aura.alarm.AlarmReceiver
import timber.log.Timber
import kotlin.time.Duration.Companion.minutes

class NotificationScheduler {

    companion object {
        val DEFAULT_INTERVAL = 15.minutes.inWholeMilliseconds
    }

    fun scheduleNotification(context: Context, interval: Long = DEFAULT_INTERVAL) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAtMillis = System.currentTimeMillis() + interval
        Timber.d("Show next notification at $triggerAtMillis")
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            interval,
            pendingIntent
        )
    }
}