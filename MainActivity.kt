package com.kidshield.childapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.kidshield.childapp.ui.theme.KidShieldChildAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initialize Firebase
        enableEdgeToEdge()

        // Ensure FCM token is stored manually on app launch
        ChildFirebaseMessagingService().ensureTokenIsStoredManually(this)

        setContent {
            KidShieldChildAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val usagePermissionGranted = remember { mutableStateOf(false) }

    // Check permission on start
    LaunchedEffect(Unit) {
        usagePermissionGranted.value = hasUsageAccessPermission(context)
    }

    // Automatically start service when permission is granted
    LaunchedEffect(usagePermissionGranted.value) {
        if (usagePermissionGranted.value) {
            val intent = Intent(context, AppUsageForegroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val intent = Intent(context, ChildProfileActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Create Child Profile")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            try {
                val fileInputStream = context.openFileInput("child_profile.txt")
                val bufferedReader = fileInputStream.bufferedReader()
                val childProfile = bufferedReader.readText()
                fileInputStream.close()
                Toast.makeText(context, childProfile, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "No child profile found", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("View Child Profile")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            context.startActivity(intent)
        }) {
            Text("Grant Usage Access Permission")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val stopIntent = Intent(context, AppUsageForegroundService::class.java)
            context.stopService(stopIntent)
            Toast.makeText(context, "Tracking stopped", Toast.LENGTH_SHORT).show()
        }) {
            Text("Stop Usage Tracking Service")
        }
    }
}

fun hasUsageAccessPermission(context: Context): Boolean {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
    val currentTime = System.currentTimeMillis()
    val usageStats = usageStatsManager.queryUsageStats(
        android.app.usage.UsageStatsManager.INTERVAL_DAILY,
        currentTime - 1000 * 1000,
        currentTime
    )
    return usageStats != null && usageStats.isNotEmpty()
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    KidShieldChildAppTheme {
        MainScreen()
    }
}
