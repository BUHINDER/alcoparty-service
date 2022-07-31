package ru.buhinder.alcopartyservice.controller.advice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import ru.buhinder.alcopartyservice.controller.advice.dto.AlcoholicErrorCode.UNKNOWN_EXCEPTION
import ru.buhinder.alcopartyservice.controller.advice.dto.ErrorCode

open class AlcoholicApiException(
    open val responseStatus: HttpStatus = INTERNAL_SERVER_ERROR,
    val code: ErrorCode = UNKNOWN_EXCEPTION,
    override val message: String,
    val payload: Map<String, Any>,
) : RuntimeException(message)
