package org.example.project.platform

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

actual class BatteryInfo actual constructor() {
    actual fun getBatteryLevel(): Int? {
        val context = AndroidPlatformContext.get() ?: return null
        val manager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val direct = manager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        if (direct != null && direct >= 0) return direct

        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) ((level * 100f) / scale).toInt() else null
    }

    actual fun isCharging(): Boolean {
        val context = AndroidPlatformContext.get() ?: return false
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
    }
}
