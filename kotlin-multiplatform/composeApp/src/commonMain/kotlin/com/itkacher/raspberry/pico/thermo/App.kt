package com.itkacher.raspberry.pico.thermo

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val sensor = UdpServer()
    val messages = MutableSharedFlow<TemperatureData>()
    coroutineScope.launch {
        sensor.listenUdpMessages().collect {
            messages.emit(it)
        }
    }
    // No architecture. Keep It Simple, Stupid. It's a demo
    val sensorMap = remember { mutableStateOf(HashMap<String, TemperatureData>()) }
    val newSensorData = messages.collectAsState(initial = null).value
    newSensorData?.let { data ->
        sensorMap.value[data.macAddress] = data // Update or add new data by MAC address
    }

    ThermoSensorAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Thermo Sensor Data") },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                )
            },
            content = {
                SensorListView(sensorData = sensorMap.value.values.toList())
            }
        )
    }
}