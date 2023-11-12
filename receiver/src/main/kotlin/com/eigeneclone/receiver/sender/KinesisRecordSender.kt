package com.eigeneclone.receiver.sender

import com.amazonaws.services.kinesis.producer.KinesisProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.ByteBuffer


@Component
class KinesisRecordSender(
    private val producer: KinesisProducer,
    @Value("\${kinesis.name}")
    private val kinesisStreamName: String,
): RecordSender {

    private val logger: Logger = LoggerFactory.getLogger(KinesisRecordSender::class.java)
    override fun sendRecord(record: String, partitionKey: String?) {
        logger.info("partitionKey: $partitionKey")
        logger.info("Sending record: $record")

        val byteRecord = ByteBuffer.wrap(record.toByteArray())

        producer.addUserRecord(kinesisStreamName, partitionKey?:System.currentTimeMillis().toString(), byteRecord)
    }

}