package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.platform.BatteryInfo
import org.example.project.platform.DeviceInfo
import org.example.project.platform.NetworkMonitor

class PlatformViewModel(
    private val deviceInfo: DeviceInfo,
    private val networkMonitor: NetworkMonitor,
    private val batteryInfo: BatteryInfo
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        PlatformUiState(
            deviceName = deviceInfo.getDeviceName(),
            osVersion = deviceInfo.getOsVersion(),
            platformName = deviceInfo.getPlatformName(),
            deviceType = deviceInfo.getDeviceType(),
            isConnected = networkMonitor.isConnected(),
            batteryLevel = batteryInfo.getBatteryLevel(),
            isCharging = batteryInfo.isCharging()
        )
    )
    val uiState: StateFlow<PlatformUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            networkMonitor.observeConnectivity().collect { connected ->
                _uiState.update { it.copy(isConnected = connected) }
            }
        }
    }

    fun refreshPlatformInfo() {
        _uiState.update {
            it.copy(
                deviceName = deviceInfo.getDeviceName(),
                osVersion = deviceInfo.getOsVersion(),
                platformName = deviceInfo.getPlatformName(),
                deviceType = deviceInfo.getDeviceType(),
                isConnected = networkMonitor.isConnected(),
                batteryLevel = batteryInfo.getBatteryLevel(),
                isCharging = batteryInfo.isCharging()
            )
        }
    }
}
