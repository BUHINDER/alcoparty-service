package ru.buhinder.alcopartyservice.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "minio")
data class MinioProperties(
    val user: String,
    val password: String,
    val url: String,
    val port: Int,
    val bucket: String,
)
