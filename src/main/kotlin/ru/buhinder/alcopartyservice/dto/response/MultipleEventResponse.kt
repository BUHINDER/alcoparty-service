package ru.buhinder.alcopartyservice.dto.response

import java.util.UUID

data class MultipleEventResponse(
    val event: EventResponse,
    val images: List<UUID>,
    val isParticipant: Boolean,
)
