package com.eigeneclone.transmitter.emitter

import com.eigeneclone.transmitter.data.TypedClientData

interface TransmitRecordEmitter {

    fun emit(shardId: String, typedCliendDatas: List<TypedClientData>) {
        typedCliendDatas.forEach { emit(shardId, it) }
    }

    fun emit(shardId: String, typedClientData: TypedClientData)
}