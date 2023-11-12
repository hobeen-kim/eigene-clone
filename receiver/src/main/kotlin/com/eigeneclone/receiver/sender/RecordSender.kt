package com.eigeneclone.receiver.sender

interface RecordSender {

    fun sendRecord(record: String, partitionKey: String?)
}