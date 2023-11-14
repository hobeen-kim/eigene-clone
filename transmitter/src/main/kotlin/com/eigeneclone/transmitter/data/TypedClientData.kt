package com.eigeneclone.transmitter.data

import java.time.LocalDateTime

class TypedClientData(
    val cuid: String,
    val type: String,
    val minSeqNum: String,
    val serverTime: LocalDateTime?,
    val data: List<String>
) {
    fun size(): Int {
        return data.size
    }

    fun isEmpty(): Boolean {
        return data.isEmpty()
    }
}