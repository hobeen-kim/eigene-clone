package com.eigeneclone.transmitter.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client


@Configuration
class S3Config(
    @Value("\${aws.access-key}") val accessKey: String,
    @Value("\${aws.secret-key}") val secretKey: String
) {

    private fun getCredentialChain(): AwsCredentialsProvider {
        val awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey)

        return StaticCredentialsProvider.create(awsBasicCredentials)
    }
    @Bean
    fun s3Client(): S3Client = S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(getCredentialChain())
            .build()


}