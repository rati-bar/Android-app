package com.screetime.parent

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScreenTimeParentApp : Application() {

    companion object {
        private const val TAG = "ScreenTimeParentApp"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Parent app starting...")

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            Log.i(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase", e)
        }
    }
}
