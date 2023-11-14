package com.eigeneclone.transmitter.runner

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import software.amazon.kinesis.coordinator.Scheduler

@Component
class KinesisRunner(
    private val kinesisConsumer: Scheduler,
): CommandLineRunner {
    override fun run(vararg args: String?) {
        kinesisConsumer.run()
    }
}