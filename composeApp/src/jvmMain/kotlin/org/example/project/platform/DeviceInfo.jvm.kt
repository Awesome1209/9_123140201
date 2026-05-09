package org.example.project.platform

actual class DeviceInfo actual constructor() {
    actual fun getDeviceName(): String = System.getProperty("os.name") ?: "Desktop"
    actual fun getOsVersion(): String = "${System.getProperty("os.name") ?: "Desktop"} ${System.getProperty("os.version") ?: ""}".trim()
    actual fun getPlatformName(): String = "Desktop JVM"
    actual fun getDeviceType(): String = "Desktop"
}
