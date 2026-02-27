package se.ahlen.vinkallaren

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VinkallarenApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize app-level components
    }
}
