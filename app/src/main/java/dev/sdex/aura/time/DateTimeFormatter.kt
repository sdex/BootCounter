package dev.sdex.aura.time

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateTimeFormatter {

    companion object {
        const val NOTIFICATION_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss"
        const val EVENTS_TIME_FORMAT = "dd/MM/yyyy"
    }

    fun format(time: Long, format: String): String {
        val dateFormat = SimpleDateFormat(format, Locale.UK)
        return dateFormat.format(Date(time))
    }

    fun formatDuration(time: Long): String {
        val seconds = time / 1000
        val HH: Long = seconds / 3600
        val MM: Long = (seconds % 3600) / 60
        val SS: Long = seconds % 60
        return String.format(Locale.UK, "%02d:%02d:%02d", HH, MM, SS)
    }
}