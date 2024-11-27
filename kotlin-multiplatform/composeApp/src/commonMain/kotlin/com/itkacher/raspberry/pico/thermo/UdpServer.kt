package com.itkacher.raspberry.pico.thermo

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
object UdpServer {

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun listenUdpMessages(): Flow<TemperatureData> {
        val temperatureData = MutableSharedFlow<TemperatureData>()
        withContext(Dispatchers.IO) {
            print("Trying to open")
            val selectorManager = SelectorManager(Dispatchers.IO)
            val serverSocket = aSocket(selectorManager)
                .udp()
                .bind(InetSocketAddress("0.0.0.0", 5468))
            println("UDP server is listening on ${serverSocket.localAddress}")
            while (isActive) {
                val datagram = serverSocket.receive()
                val message = datagram.packet.readText()
                val temperature = Json.decodeFromString<TemperatureData>(message)
                temperatureData.emit(temperature)
                println("Received message: $message from ${datagram.address}")
            }
            serverSocket.dispose()
        }
        return temperatureData.asSharedFlow()
    }
}