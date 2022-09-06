package ru.buhinder.alcopartyservice.dto.response

import java.util.UUID

data class MultipleEventResponse(
    val event: EventResponse,
    val image: UUID?,
    val isParticipant: Boolean,
)
