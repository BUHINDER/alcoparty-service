package ru.buhinder.alcopartyservice.config

import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.buhinder.alcopartyservice.config.properties.MinioProperties

@Configuration
class MinioConfig(
    private val minioProperties: MinioProperties,
) {

    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .credentials(minioProperties.user, minioProperties.password)
            .endpoint(minioProperties.url, minioProperties.port, false)
            .build()
    }

}
