package com.itkacher.raspberry.pico.thermo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform