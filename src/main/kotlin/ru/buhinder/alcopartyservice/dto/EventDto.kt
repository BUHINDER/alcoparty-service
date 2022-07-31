package ru.buhinder.alcopartyservice.dto

import ru.buhinder.alcopartyservice.entity.enums.EventType
import java.util.UUID

data class EventDto(
    val alcoholicsIds: Set<UUID>,
    val info: String,
    val photosIds: Set<UUID>,
    val type: EventType,
    val location: String,
    val startDate: Long,
    val endDate: Long? = null,
)
