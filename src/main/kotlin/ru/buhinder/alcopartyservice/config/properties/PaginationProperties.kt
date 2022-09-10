package ru.buhinder.alcopartyservice.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "pagination.default")
data class PaginationProperties(
    val page: Int,
    val pageSize: Int,
)
