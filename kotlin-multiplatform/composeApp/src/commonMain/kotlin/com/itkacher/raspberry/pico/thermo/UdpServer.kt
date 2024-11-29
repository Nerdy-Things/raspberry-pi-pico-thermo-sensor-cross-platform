package com.itkacher.raspberry.pico.thermo

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import kotlin.concurrent.Volatile
import kotlin.coroutines.suspendCoroutine

object UdpServer {
    @Volatile
    private var serverSocket: BoundDatagramSocket? = null
    private val temperatureData = MutableSharedFlow<TemperatureData>()

    fun listenUdpMessages(scope: CoroutineScope): Flow<TemperatureData> {
        scope.launch(Dispatchers.IO + CoroutineName("BackgroundCoroutine")) {
            val selectorManager = SelectorManager(Dispatchers.IO)
            serverSocket?.dispose()
            val newSocket = aSocket(selectorManager)
                .udp()
                .bind(InetSocketAddress("0.0.0.0", 5468))
            serverSocket = newSocket
            println("UDP server is listening on ${serverSocket?.localAddress}")
            while (isActive && newSocket == serverSocket) {
                val datagram = serverSocket?.receive() ?: break
                val message = datagram.packet.readText()
                val temperature = Json.decodeFromString<TemperatureData>(message)
                temperatureData.emit(temperature)
                println("Received message: $message from ${datagram.address}")
            }
            println("Cleared socket")
            serverSocket?.dispose()
            serverSocket = null
        }
        return temperatureData
    }
}
