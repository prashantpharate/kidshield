package com.kidshield.childapp

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun requestDeviceAdmin(context: Context) {
    Toast.makeText(context, "Requesting device admin...", Toast.LENGTH_SHORT).show()
    val compName = ComponentName(context, DeviceAdminReceiver::class.java)
    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
        putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Allow KidShield to lock screen remotely")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
