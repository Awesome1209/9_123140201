package org.example.project.platform

expect class DeviceInfo() {
    fun getDeviceName(): String
    fun getOsVersion(): String
    fun getPlatformName(): String
    fun getDeviceType(): String
}
