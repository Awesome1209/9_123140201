package org.example.project.platform

import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomPad

actual class DeviceInfo actual constructor() {
    actual fun getDeviceName(): String = UIDevice.currentDevice.name
    actual fun getOsVersion(): String = "iOS ${UIDevice.currentDevice.systemVersion}"
    actual fun getPlatformName(): String = "iOS"
    actual fun getDeviceType(): String = if (UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad) "Tablet" else "Phone"
}
