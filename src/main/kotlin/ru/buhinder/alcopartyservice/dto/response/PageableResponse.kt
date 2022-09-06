package ru.buhinder.alcopartyservice.dto.response

data class PageableResponse<T>(
    val data: List<T>,
    val pagination: Pagination
)
