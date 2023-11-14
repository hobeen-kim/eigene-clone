package com.eigeneclone.transmitter.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LogFileNames {

    companion object {

        private val RB_FOLDER_DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        private val FILE_DTF = DateTimeFormatter.ofPattern("yyyyMMddHH")

        fun getLogFolderName(cuid: String, date: LocalDateTime): String {
            return "$cuid/${date.format(RB_FOLDER_DTF)}/"
        }

        fun getLogFolderName(cuid: String, type:String, date: LocalDateTime): String {
            return "$cuid/$type/${date.format(RB_FOLDER_DTF)}/"
        }

        fun getLogFileName(type: String, date: LocalDateTime, shardId: String, minSeqNum: String): String {
            return "${type}_${date.format(FILE_DTF)}_${shardId}_${minSeqNum}.json.gz"
        }
    }
}