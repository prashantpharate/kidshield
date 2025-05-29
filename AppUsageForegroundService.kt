package com.kidshield.childapp
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AppUsageForegroundService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val CHANNEL_ID = "AppUsageTrackingChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("Tracking app usage..."))

        // Start repeated usage tracking every 5 minutes (adjust as needed)
        scope.launch {
            while (isActive) {
                trackAndSendUsage()
                delay(TimeUnit.MINUTES.toMillis(1))
            }
        }
    }

    private suspend fun trackAndSendUsage() {
        try {
            val tracker = ForegroundAppTracker(applicationContext)
            val usageStats = tracker.getAppUsageStats(durationInMinutes = 1)

            val timestamp = System.currentTimeMillis().toString()
            val db = FirebaseFirestore.getInstance()

            db.collection("foreground_app_usage")
                .document("session_$timestamp")
                .set(mapOf("timestamp" to timestamp, "apps" to usageStats))
                .await() // Use kotlinx-coroutines-play-services to await Firestore tasks

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Usage Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("KidShield Tracking")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // replace with your app icon
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
