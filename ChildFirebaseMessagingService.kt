package com.kidshield.childapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ChildFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("KidShield", "FCM Token refreshed: $token")
        saveTokenToFirestore(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("KidShield", "FCM Data: ${remoteMessage.data}")

        val command = remoteMessage.data["type"] ?: return

        when (command) {
            "lock_screen" -> {
                showNotification("KidShield", "Locking screen remotely")
                lockScreen()
            }
            "unlock_screen" -> {
                showNotification("KidShield", "Unlocking screen remotely")
                unlockScreen()
            }
            else -> Log.d("KidShield", "Unknown command: $command")
        }
    }

    private fun lockScreen() {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val compName = ComponentName(this, DeviceAdminReceiver::class.java)
        Log.d("KidShield", "isAdminActive = ${dpm.isAdminActive(compName)}")
        if (dpm.isAdminActive(compName)) {
            wakeScreen()
            dpm.lockNow()
        } else {
            Log.d("KidShield", "Device admin not active — requesting admin permission")
            requestDeviceAdmin(this)
        }
    }

    private fun unlockScreen() {
        wakeScreen()

        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

        Log.d("KidShield", "Sent user to home screen to simulate unlock")
    }

    private fun wakeScreen() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            powerManager.isInteractive
        } else {
            powerManager.isScreenOn
        }

        if (!isScreenOn) {
            val wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "KidShield:WakeLock"
            )
            wakeLock.acquire(3000)
            wakeLock.release()
        }
    }

    private fun showNotification(title: String, message: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "kidshield_channel",
                "KidShield Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, "kidshield_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification) // Ensure this icon exists in res/drawable
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    // 🔁 Save token to Firestore
    private fun saveTokenToFirestore(token: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("tokens")
            .document("child_token")
            .set(mapOf("token" to token))
            .addOnSuccessListener {
                Log.d("KidShield", "Token saved to Firestore successfully")
            }
            .addOnFailureListener {
                Log.e("KidShield", "Failed to save token: ${it.message}")
            }
    }

    // ✅ Public function to call manually from your activity
    fun ensureTokenIsStoredManually(context: Context) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d("KidShield", "Manually checking/storing token: $token")
            val db = FirebaseFirestore.getInstance()
            db.collection("tokens")
                .document("child_token")
                .set(mapOf("token" to token))
                .addOnSuccessListener {
                    Log.d("KidShield", "Token manually stored in Firestore")
                }
                .addOnFailureListener {
                    Log.e("KidShield", "Failed to store token manually: ${it.message}")
                }
        }
    }
}
