package com.eigeneclone.receiver.jsonhandler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

class JsonHandler (
     val jsonNode: JsonNode
){
    companion object {
        val objectMapper = ObjectMapper()
    }

    init {
        if (!valid()) throw IllegalArgumentException("Invalid JSON")
    }

    constructor(jsonString: String, ip: String): this(objectMapper.readTree(jsonString)) {
        addIpAddress(ip)
    }

    private fun valid(): Boolean {
        return jsonNode.has("cuid")
    }

    fun addIpAddress(ip: String): JsonNode {

        jsonNode as ObjectNode

        return jsonNode.apply {
            put("ip", ip)
        }
    }

    fun getValue(key: String): String? = jsonNode.get(key)?.asText()

    override fun toString(): String {
        return objectMapper.writeValueAsString(jsonNode)
    }
}