package com.kidshield.childapp

import android.app.Application
import com.google.firebase.FirebaseApp

class KidShieldChild : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}