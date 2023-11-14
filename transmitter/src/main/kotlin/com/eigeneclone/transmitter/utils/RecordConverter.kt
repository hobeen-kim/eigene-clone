package com.eigeneclone.transmitter.utils

import com.eigeneclone.transmitter.data.RBRecord
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import software.amazon.kinesis.retrieval.KinesisClientRecord
import java.nio.charset.Charset

class RecordConverter {

    val decoder = Charset.forName("UTF-8").newDecoder()
    val mapper = ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)

    fun convert(record: KinesisClientRecord): RBRecord {

        val json = decoder.decode(record.data()).toString()

        val root = mapper.readTree(json) as ObjectNode

        return RBRecord(
            seqNum = record.sequenceNumber(),
            cuid = root.get("cuid").asText(),
            type = root.get("type").asText(),
            serverTime = root.get("serverTime").asText(),
            json = json
        )
    }
}