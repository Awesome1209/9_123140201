package org.example.project.platform

actual class BatteryInfo actual constructor() {
    actual fun getBatteryLevel(): Int? = null
    actual fun isCharging(): Boolean = false
}
