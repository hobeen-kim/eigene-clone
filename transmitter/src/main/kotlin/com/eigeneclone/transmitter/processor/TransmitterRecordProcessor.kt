package com.eigeneclone.transmitter.processor

import com.eigeneclone.transmitter.data.TransmitRecordQueue
import com.eigeneclone.transmitter.emitter.TransmitRecordEmitter
import com.eigeneclone.transmitter.utils.RecordConverter
import org.slf4j.LoggerFactory
import software.amazon.kinesis.lifecycle.events.*
import software.amazon.kinesis.processor.RecordProcessorCheckpointer
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.retrieval.KinesisClientRecord

class TransmitterRecordProcessor(
    var shardId: String? = null,
    val recordEmitter: TransmitRecordEmitter,
    val transmitRecordQueue: TransmitRecordQueue,
    val recordConverter: RecordConverter
): ShardRecordProcessor {

    private val logger = LoggerFactory.getLogger(TransmitterRecordProcessor::class.java)
    private var nextCheckpointTimeInMillis = System.currentTimeMillis() + CHECKPOINT_INTERVAL_MILLIS

    companion object {
        private const val CHECKPOINT_INTERVAL_MILLIS = 1800000L
        private const val NUM_RETRIES = 3
    }

    private fun needToCheckout(): Boolean {
//        return System.currentTimeMillis() > nextCheckpointTimeInMillis ||
//                transmitRecordQueue.exceedsCapacity()
        return true
    }

    private fun checkpoint(checkpointer: RecordProcessorCheckpointer?) {
        logger.info("Checkpointing shard $shardId")

        for(i in 0 until NUM_RETRIES) {
            try{
                recordEmitter.emit(shardId!!, transmitRecordQueue.getTypedClientDatas())
                transmitRecordQueue.flush()
                checkpointer!!.checkpoint()
                break
            }catch (e: Exception) {
                logger.error("Caught exception when checkpointing shard $shardId - attempts : $i", e)
            }
        }
    }

    override fun initialize(initializationInput: InitializationInput?) {
        logger.info("Initializing record processor for shard ${initializationInput?.shardId()}")
        this.shardId = initializationInput?.shardId()
    }

    override fun processRecords(processRecordsInput: ProcessRecordsInput?) {

        val records = processRecordsInput?.records()

        logger.info("Processing ${records?.size} records from $shardId")

        addRecordsToQueue(records)

        if(needToCheckout()) {
            checkpoint(processRecordsInput!!.checkpointer())
            nextCheckpointTimeInMillis = System.currentTimeMillis() + CHECKPOINT_INTERVAL_MILLIS
        }
    }

    private fun addRecordsToQueue(records: MutableList<KinesisClientRecord>?) {
        records?.forEach { record ->
            recordConverter.convert(record).let { transmitRecordQueue.add(it) }
        }
    }

    override fun leaseLost(leaseLostInput: LeaseLostInput?) {
        logger.info("Lost lease, so terminating")
    }

    override fun shardEnded(shardEndedInput: ShardEndedInput?) {
        logger.info("Reached shard end checkpointing")
        checkpoint(shardEndedInput!!.checkpointer())
    }

    override fun shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput?) {
        logger.info("Scheduler is shutting down, so checkpointing")
        checkpoint(shutdownRequestedInput!!.checkpointer())
    }
}