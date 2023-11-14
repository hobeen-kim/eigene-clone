package com.eigeneclone.transmitter.config

import com.eigeneclone.transmitter.processor.TransmitterRecordProcessorFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import software.amazon.kinesis.coordinator.Scheduler
import software.amazon.kinesis.retrieval.polling.PollingConfig
import java.util.*


@Configuration
class KinesisConfig(
    @Value("\${aws.access-key}") val accessKey: String,
    @Value("\${aws.secret-key}") val secretKey: String,
    @Value("\${aws.kinesis.stream-name}") val streamName: String,
    @Value("\${aws.kinesis.application-name}") val applicationName: String,
    @Value("\${aws.kinesis.endpoint}") val endpoint: String,
    @Value("\${aws.kinesis.idle-time-between-reads-in-millis}") val idleTimeBetweenReadsInMillis: Long,
    @Value("\${aws.kinesis.region-id}") val regionId: String,
    private val recordProcessorFactory: TransmitterRecordProcessorFactory
){

    private fun getCredentialChain(): AwsCredentialsProvider {
        val awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey)

        return StaticCredentialsProvider.create(awsBasicCredentials)
    }

    fun dynamoDbAsyncClient(): DynamoDbAsyncClient {
        return DynamoDbAsyncClient.builder().credentialsProvider(getCredentialChain()).region(Region.of(regionId))
            .build()
    }

    fun cloudWatchAsyncClient(): CloudWatchAsyncClient {
        return CloudWatchAsyncClient.builder().credentialsProvider(getCredentialChain()).region(Region.of(regionId))
            .build()
    }

    fun kinesisAsyncClient(): KinesisAsyncClient {
        return KinesisAsyncClient.builder().credentialsProvider(getCredentialChain())
            .region(Region.of(regionId)).build()
    }

    private fun getWorkerId(): String {
        return "transmitter-worker: ${UUID.randomUUID()}"
    }


    @Bean
    fun kinesisConsumer(
        recordProcessorFactory: TransmitterRecordProcessorFactory?
    ): Scheduler {

        val kinesisAsyncClient = kinesisAsyncClient()

        val configBuilder = ConfigsBuilder(
            streamName, applicationName, kinesisAsyncClient,
            dynamoDbAsyncClient(), cloudWatchAsyncClient(), getWorkerId(), recordProcessorFactory!!
        )
        return Scheduler(
            configBuilder.checkpointConfig(),
            configBuilder.coordinatorConfig(),
            configBuilder.leaseManagementConfig(),
            configBuilder.lifecycleConfig(),
            configBuilder.metricsConfig(),
            configBuilder.processorConfig(),
            configBuilder.retrievalConfig().retrievalSpecificConfig(PollingConfig(streamName, kinesisAsyncClient))
        )
    }



}