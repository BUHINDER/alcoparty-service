package ru.buhinder.alcopartyservice.service

import kotlin.math.ceil
import org.springframework.stereotype.Component
import ru.buhinder.alcopartyservice.dto.response.Pagination

@Component
class PaginationService {

    fun createPagination(total: Long, page: Int, pageSize: Int): Pagination {
        return Pagination(total, page, pageSize, ceil(total.toDouble() / pageSize).toLong())
    }
}
