package org.example.project.platform

import android.os.Build
import android.content.res.Resources
import kotlin.math.pow
import kotlin.math.sqrt

actual class DeviceInfo actual constructor() {
    actual fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.titlecase() }
        val model = Build.MODEL.orEmpty()
        return if (model.startsWith(manufacturer, ignoreCase = true)) model else "$manufacturer $model"
    }

    actual fun getOsVersion(): String = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"

    actual fun getPlatformName(): String = "Android"

    actual fun getDeviceType(): String {
        val metrics = Resources.getSystem().displayMetrics
        val widthInches = metrics.widthPixels / metrics.xdpi
        val heightInches = metrics.heightPixels / metrics.ydpi
        val diagonal = sqrt(widthInches.pow(2) + heightInches.pow(2))
        return if (diagonal >= 7.0) "Tablet" else "Phone"
    }
}
