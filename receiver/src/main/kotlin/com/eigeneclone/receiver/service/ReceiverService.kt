package com.eigeneclone.receiver.service

import com.eigeneclone.receiver.jsonhandler.JsonHandler
import com.eigeneclone.receiver.sender.RecordSender
import org.springframework.stereotype.Service

@Service
class ReceiverService constructor(
    private val recordSender: RecordSender
) {
    fun log(rawLog: String, ip: String) {

        val jsonHandler = JsonHandler(rawLog, ip)

        recordSender.sendRecord(jsonHandler.toString(), jsonHandler.getValue("pcid")!!)
    }
}