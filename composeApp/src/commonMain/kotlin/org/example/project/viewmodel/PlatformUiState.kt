package org.example.project.viewmodel

data class PlatformUiState(
    val deviceName: String = "Unknown Device",
    val osVersion: String = "Unknown OS",
    val platformName: String = "Unknown Platform",
    val deviceType: String = "Unknown",
    val isConnected: Boolean = true,
    val batteryLevel: Int? = null,
    val isCharging: Boolean = false
) {
    val networkLabel: String
        get() = if (isConnected) "Online" else "Offline"

    val syncAvailabilityLabel: String
        get() = if (isConnected) "Remote Sync Available" else "Local Notes Still Available"

    val batteryLabel: String
        get() = batteryLevel?.let { "$it%" } ?: "Unknown"

    val chargingLabel: String
        get() = if (isCharging) "Yes" else "No"
}
