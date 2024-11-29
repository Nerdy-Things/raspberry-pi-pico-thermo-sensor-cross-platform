package com.itkacher.raspberry.pico.thermo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TemperatureData(
    @SerialName("name")
    val name: String? = null,
    @SerialName("temperature")
    val temperature: Double,
    @SerialName("mac_address")
    val macAddress: String? = null,
)
