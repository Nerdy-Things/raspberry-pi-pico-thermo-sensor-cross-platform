package com.itkacher.raspberry.pico.thermo

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(DelicateCoroutinesApi::class)
@Composable
@Preview
fun App() {
    // No architecture. Keep It Simple, Stupid. It's a demo
    val coroutineScope = rememberCoroutineScope()
    val sensorMap = mutableMapOf<String, TemperatureData>()
    val state = mutableStateOf<List<TemperatureData>>(emptyList())
    val messageFlow = UdpServer.listenUdpMessages(coroutineScope)
    val messages = remember { state }

    coroutineScope.launch {
        messageFlow.collect {
            // Update or add new data by MAC address
            sensorMap[it.macAddress ?: it.name ?: ""] = it
            state.value = sensorMap.values.sortedBy { it.name }
        }
    }

    ThermoSensorAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Temperature Sensor Data") },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                )
            },
            content = {
                SensorListView(sensorData = messages.value)
            }
        )
    }
}