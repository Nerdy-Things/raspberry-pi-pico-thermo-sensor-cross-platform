package com.itkacher.raspberry.pico.thermo

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

object UdpServer {
    private var serverSocket: BoundDatagramSocket? = null
    private val temperatureData = MutableSharedFlow<TemperatureData>()
    private val mutex = Mutex()

    fun listenUdpMessages(scope: CoroutineScope): Flow<TemperatureData> {
        scope.launch(Dispatchers.IO + CoroutineName("BackgroundCoroutine")) {
            mutex.withLock {
                try {
                    val selectorManager = SelectorManager(Dispatchers.IO)
                    serverSocket?.dispose()
                    val newSocket = aSocket(selectorManager)
                        .udp()
                        .bind(InetSocketAddress("0.0.0.0", 5468))
                    serverSocket = newSocket
                    println("UDP server is listening on ${serverSocket?.localAddress}")
                    while (isActive) {
                        val datagram = serverSocket?.receive() ?: break
                        val message = datagram.packet.readText()
                        val temperature = Json.decodeFromString<TemperatureData>(message)
                        try {
                            temperatureData.emit(temperature)
                            println("Received message: $message from ${datagram.address}")
                        } catch (e: Exception) {
                            println("Error emitting temperature data: ${e.message}")
                        }
                    }
                } catch (e: Exception) {
                    println("Error during UDP listening: ${e.message}")
                } finally {
                    serverSocket?.dispose()
                    serverSocket = null
                    println("Socket disposed.")
                }
            }
        }
        return temperatureData
    }
}
