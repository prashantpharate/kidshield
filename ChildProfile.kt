package com.kidshield.childapp

data class ChildProfile(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val screenTimeLimit: Int = 0, // in minutes
    val appUsageLimits: MutableMap<String, Int> = mutableMapOf() // app package name to time limit in minutes
)