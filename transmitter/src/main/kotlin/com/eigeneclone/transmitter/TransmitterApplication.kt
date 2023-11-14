package com.eigeneclone.transmitter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TransmitterApplication

fun main(args: Array<String>) {
    runApplication<TransmitterApplication>(*args)
}
