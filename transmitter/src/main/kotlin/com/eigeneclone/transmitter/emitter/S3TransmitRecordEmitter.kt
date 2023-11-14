package com.eigeneclone.transmitter.emitter

import com.eigeneclone.transmitter.data.TypedClientData
import com.eigeneclone.transmitter.utils.BytaUtils
import com.eigeneclone.transmitter.utils.LogFileNames
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

@Component
class S3TransmitRecordEmitter(
    private val s3Client: S3Client,
    @Value("\${aws.s3.bucket-name}") val bucketName: String
): TransmitRecordEmitter {
    override fun emit(shardId: String, typedClientData: TypedClientData) {

        if(typedClientData.isEmpty()) {
            return
        }

        val dataTime = typedClientData.serverTime ?: LocalDateTime.now()

        val folderName = LogFileNames.getLogFolderName(typedClientData.cuid, typedClientData.type, dataTime)
        val fileName = LogFileNames.getLogFileName(typedClientData.type, dataTime, shardId, typedClientData.minSeqNum)

        val key = folderName + fileName

        val dump = BytaUtils.compressByGzip(BytaUtils.getBytaWithNewlines(typedClientData.data))

        // Get the Amazon S3 filename
        val s3URI = "s3://$bucketName/$key"
        try {
            ByteArrayInputStream(dump).use { bais ->

                val putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()

                val requestBody = RequestBody.fromInputStream(bais, dump.size.toLong())

                s3Client.putObject(putObjectRequest, requestBody)

            }
        } catch (e: Exception) {
            throw RuntimeException(String.format("Caught exception when uploading file: %s", s3URI), e)
        }

    }
}