package com.eigeneclone.receiver.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.kinesis.producer.KinesisProducer
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KinesisConfig(
    @Value("\${aws.access-key}")
    val accessKey: String,
    @Value("\${aws.secret-key}")
    val secretKey: String,
    @Value("\${kinesis.region}")
    val region: String,
) {

    @Bean
    fun kinesisProducer(): KinesisProducer {

        val kinesisProducerConfiguration = KinesisProducerConfiguration()

        kinesisProducerConfiguration.region = region
        kinesisProducerConfiguration.credentialsProvider = AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey))

        return KinesisProducer(kinesisProducerConfiguration)
    }


}