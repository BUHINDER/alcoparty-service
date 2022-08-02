package ru.buhinder.alcopartyservice.controller.advice.dto

import ru.buhinder.alcopartyservice.controller.advice.dto.AlcoholicErrorCode.UNKNOWN_EXCEPTION
import ru.buhinder.alcopartyservice.controller.advice.exception.AlcoholicApiException

data class ErrorInfoDto(
    val code: ErrorCode,
    val message: String?,
    val payload: Map<String, Any>,
) {
    constructor(ex: AlcoholicApiException) : this(
        code = ex.code,
        message = ex.message,
        payload = ex.payload,
    )

    constructor(ex: Exception) : this(
        code = UNKNOWN_EXCEPTION,
        message = ex.message,
        payload = mapOf("exception" to ex.javaClass.simpleName),
    )
}
