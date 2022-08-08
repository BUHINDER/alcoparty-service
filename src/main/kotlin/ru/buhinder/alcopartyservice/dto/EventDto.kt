package ru.buhinder.alcopartyservice.dto

import ru.buhinder.alcopartyservice.entity.enums.EventType
import java.util.UUID
import javax.validation.constraints.Min
import javax.validation.constraints.Size

data class EventDto(
    val alcoholicsIds: Set<UUID>,
    @field:Size(min = 0, max = 50)
    val title: String,
    @field:Size(min = 0, max = 1000)
    val info: String,
    val photosIds: Set<UUID>,
    val type: EventType,
    @field:Size(min = 0, max = 200)
    val location: String,
    @field:Min(0)
    val startDate: Long,
    @field:Min(0)
    val endDate: Long? = null,
)
