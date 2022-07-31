package ru.buhinder.alcopartyservice.controller.advice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CONFLICT
import ru.buhinder.alcopartyservice.controller.advice.dto.AlcoholicErrorCode.CANNOT_BE_DEACTIVATED
import ru.buhinder.alcopartyservice.controller.advice.dto.ErrorCode

class EntityCannotBeDeactivatedException(
    responseStatus: HttpStatus = CONFLICT,
    code: ErrorCode = CANNOT_BE_DEACTIVATED,
    message: String,
    payload: Map<String, Any>,
) : AlcoholicApiException(
    responseStatus = responseStatus,
    code = code,
    message = message,
    payload = payload
)
