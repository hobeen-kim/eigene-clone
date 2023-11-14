package com.eigeneclone.transmitter.data

import java.time.LocalDateTime

class TypedClientStats(
    val cuid: String,
    val type: String,
    val minSeqNum: String,
    val serverTime: LocalDateTime,
    val count: Int
) {
}