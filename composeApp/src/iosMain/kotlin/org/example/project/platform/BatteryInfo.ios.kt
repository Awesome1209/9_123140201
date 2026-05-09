package org.example.project.platform

import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceBatteryStateCharging
import platform.UIKit.UIDeviceBatteryStateFull

actual class BatteryInfo actual constructor() {
    init {
        UIDevice.currentDevice.batteryMonitoringEnabled = true
    }

    actual fun getBatteryLevel(): Int? {
        val level = UIDevice.currentDevice.batteryLevel
        return if (level < 0.0) null else (level * 100).toInt()
    }

    actual fun isCharging(): Boolean {
        val state = UIDevice.currentDevice.batteryState
        return state == UIDeviceBatteryStateCharging || state == UIDeviceBatteryStateFull
    }
}
