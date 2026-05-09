package org.example.project.platform

expect class BatteryInfo() {
    fun getBatteryLevel(): Int?
    fun isCharging(): Boolean
}
