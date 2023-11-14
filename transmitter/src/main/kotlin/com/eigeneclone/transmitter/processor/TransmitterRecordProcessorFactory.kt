package com.eigeneclone.transmitter.processor

import com.eigeneclone.transmitter.data.TransmitRecordQueue
import com.eigeneclone.transmitter.emitter.TransmitRecordEmitter
import com.eigeneclone.transmitter.utils.RecordConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.processor.ShardRecordProcessorFactory

@Component
class TransmitterRecordProcessorFactory(
    private val recordEmitter: TransmitRecordEmitter,
    private val jdbcTemplate: JdbcTemplate,
    @Value("\${transmitter.queue.max-bytes}")
    private val queueMaxBytes: Int,
    @Value("\${transmitter.queue.max-records}")
    private val queueMaxRecords: Int
): ShardRecordProcessorFactory {

    val logger = LoggerFactory.getLogger(TransmitterRecordProcessorFactory::class.java)

    override fun shardRecordProcessor(): ShardRecordProcessor {
        return TransmitterRecordProcessor(
            recordEmitter = recordEmitter,
            recordConverter = RecordConverter(),
            transmitRecordQueue = TransmitRecordQueue(
                jdbcTemplate = jdbcTemplate, queueMaxBytes, queueMaxRecords)
        )
    }
}