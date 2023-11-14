package com.eigeneclone.transmitter.data

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.jdbc.core.JdbcTemplate
import java.time.format.DateTimeFormatter

class TransmitRecordQueue(
    private val jdbcTemplate: JdbcTemplate,
    private val maxBytes: Int,
    private val maxRecords: Int
    ) {

    lateinit var queueTable: String

    init {
        queueTable = "transmit_queue_" + RandomStringUtils.randomAlphabetic(10).lowercase()
        createQueueTable()
    }

    private fun createQueueTable() {
        val sql = """
            create table if not exists $queueTable
                (seqNum varchar(100) not null,
                cuid varchar(50) not null,
                type varchar(40) not null, 
                server_time timestamp,
                json varchar(65535))
                """.trimIndent()
        jdbcTemplate.execute(sql)
    }

    fun add(record: RBRecord) {
        val sql = """
            insert into $queueTable (seqNum, cuid, type, server_time, json)
            values (?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            record.seqNum,
            record.cuid,
            record.type,
            record.serverTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            record.json
        )
    }

    fun count(): Int {
        return jdbcTemplate.queryForObject("select count(*) from $queueTable", Int::class.java)!!
    }

    fun size(): Int {
        return jdbcTemplate.queryForObject("select sum(length(json)) from $queueTable", Int::class.java)!!
    }

    fun getTypedClientDatas(): List<TypedClientData> {
        return getTypedClientStats().map {
            TypedClientData(
                cuid = it.cuid,
                type = it.type,
                minSeqNum = it.minSeqNum,
                serverTime = it.serverTime,
                data = getData(it)
            )
        }
    }

    private fun getTypedClientStats(): List<TypedClientStats> {
        val sql = """
            select cuid, type, min(seqNum) as minSeqNum, min(server_time) as serverTime, count(*) as count
            from $queueTable
            group by cuid, type
            order by cuid
        """.trimIndent()

        return jdbcTemplate.query(sql) { rs, _ ->
            TypedClientStats(
                cuid = rs.getString("cuid"),
                type = rs.getString("type"),
                minSeqNum = rs.getString("minSeqNum"),
                serverTime = rs.getTimestamp("serverTime").toLocalDateTime(),
                count = rs.getInt("count")
            )
        }
    }

    private fun getData(typedClientStats: TypedClientStats): List<String> {
        val sql = """
            select json
            from $queueTable
            where cuid = ? and type = ?
        """.trimIndent()

        return jdbcTemplate.queryForList(
            sql,
            String::class.java,
            typedClientStats.cuid,
            typedClientStats.type
        )
    }

    fun flush() {
        val sql = """
            truncate table $queueTable
        """.trimIndent()
        jdbcTemplate.execute(sql)
    }

    fun exceedsCapacity(): Boolean {
        return size() >= maxBytes || count() >= maxRecords
    }
}