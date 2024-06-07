package dev.sdex.aura

import android.app.Application
import dev.sdex.aura.data.DataStore
import timber.log.Timber

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        DataStore.init(this)
        Timber.plant(Timber.DebugTree())
    }
}