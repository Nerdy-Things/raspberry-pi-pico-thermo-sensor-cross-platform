package com.itkacher.raspberry.pico.thermo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import pico_thermo_sensor.composeapp.generated.resources.Res
import pico_thermo_sensor.composeapp.generated.resources.compose_multiplatform
import kotlin.text.set

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