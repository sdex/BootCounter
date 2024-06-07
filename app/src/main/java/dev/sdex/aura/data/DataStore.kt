package dev.sdex.aura.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object DataStore {

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(
            "preferences",
            Context.MODE_PRIVATE
        )
    }

    fun onNotificationDismissed() {
        sharedPreferences.edit {
            putBoolean("restoreNotification", false)
        }
        incrementTotalDismissals()
    }

    fun onNotificationShown() {
        sharedPreferences.edit {
            putBoolean("restoreNotification", true)
        }
    }

    fun shouldRestoreNotification(): Boolean =
        sharedPreferences.getBoolean("restoreNotification", false)

    // TODO store in more advanced way, e.g. sqlite database
    fun getBootEvents(): List<Long> =
        sharedPreferences.getStringSet("bootEvents", emptySet())
            ?.map { it.toLong() }
            ?.toList()?.sorted() ?: emptyList()

    fun addBootEvent() {
        val bootEvents = getBootEvents()
        val bootEvent = System.currentTimeMillis()
        sharedPreferences.edit {
            putStringSet("bootEvents", bootEvents.map { it.toString() }
                .toMutableSet().apply { add(bootEvent.toString()) })
        }
    }

    fun setTotalAllowedDismissals(totalAllowedDismissals: Long) {
        sharedPreferences.edit {
            putLong("totalAllowedDismissals", totalAllowedDismissals)
        }
    }

    fun getTotalAllowedDismissals(): Long =
        sharedPreferences.getLong("totalAllowedDismissals", 5)

    fun setIntervalBetweenDismissals(interval: Long) {
        sharedPreferences.edit {
            putLong("intervalBetweenDismissals", interval)
        }
    }

    fun getIntervalBetweenDismissals(): Long =
        sharedPreferences.getLong("intervalBetweenDismissals", 20)

    private fun incrementTotalDismissals() {
        val totalDismissals = getTotalDismissals() + 1
        sharedPreferences.edit {
            putLong("totalDismissals", totalDismissals)
        }
    }

    fun getTotalDismissals(): Long =
        sharedPreferences.getLong("totalDismissals", 0)
}