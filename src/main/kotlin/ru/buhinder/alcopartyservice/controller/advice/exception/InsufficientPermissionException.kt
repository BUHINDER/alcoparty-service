package ru.buhinder.alcopartyservice.controller.advice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CONFLICT
import ru.buhinder.alcopartyservice.controller.advice.dto.AlcoholicErrorCode.INSUFFICIENT_PERMISSIONS
import ru.buhinder.alcopartyservice.controller.advice.dto.ErrorCode

class InsufficientPermissionException(
    responseStatus: HttpStatus = CONFLICT,
    code: ErrorCode = INSUFFICIENT_PERMISSIONS,
    message: String,
    payload: Map<String, Any>,
) : AlcoholicApiException(
    responseStatus = responseStatus,
    code = code,
    message = message,
    payload = payload
)
