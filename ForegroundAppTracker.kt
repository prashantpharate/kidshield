package com.kidshield.childapp

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context

class ForegroundAppTracker(private val context: Context) {

    @Suppress("DEPRECATION")
    fun getAppUsageStats(durationInMinutes: Int = 1): Map<String, Long> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - durationInMinutes * 60 * 1000

        val usageEvents = usageStatsManager.queryEvents(startTime, currentTime)

        val usageMap = mutableMapOf<String, Long>()
        val appStartTimeMap = mutableMapOf<String, Long>()

        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            when (event.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    appStartTimeMap[event.packageName] = event.timeStamp
                }
                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    val startTimeForApp = appStartTimeMap[event.packageName]
                    if (startTimeForApp != null && event.timeStamp > startTimeForApp) {
                        val duration = event.timeStamp - startTimeForApp
                        val currentDuration = usageMap.getOrDefault(event.packageName, 0L)
                        usageMap[event.packageName] = currentDuration + duration
                        appStartTimeMap.remove(event.packageName)
                    }
                }
            }
        }

        return usageMap.mapValues { it.value / 1000 } // convert to seconds
    }
}
