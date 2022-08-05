package ru.buhinder.alcopartyservice.model

import ru.buhinder.alcopartyservice.dto.EventDto
import java.util.UUID

data class EventModel(
    val eventDto: EventDto,
    val alcoholicId: UUID,
)
