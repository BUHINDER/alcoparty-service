package ru.buhinder.alcopartyservice.dto.response

data class Pagination(
    val total: Long,
    val page: Int,
    val pageSize: Int,
    val pageCount: Long,
)
