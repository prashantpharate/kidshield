package com.kidshield.childapp

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore

class AppUsageWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val tracker = ForegroundAppTracker(applicationContext)
            val usageStats = tracker.getAppUsageStats(durationInMinutes = 1)

            val timestamp = System.currentTimeMillis().toString()
            val db = FirebaseFirestore.getInstance()

            db.collection("foreground_app_usage")
                .document("session_$timestamp")
                .set(mapOf("timestamp" to timestamp, "apps" to usageStats))
                .addOnSuccessListener {
                    Log.d("AppUsageWorker", "Usage data sent successfully")
                }
                .addOnFailureListener {
                    Log.e("AppUsageWorker", "Failed to write usage data", it)
                }

            Result.success()
        } catch (e: Exception) {
            Log.e("AppUsageWorker", "Error collecting usage data", e)
            Result.failure()
        }
    }
}
