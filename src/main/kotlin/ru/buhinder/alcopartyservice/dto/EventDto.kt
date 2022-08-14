package ru.buhinder.alcopartyservice.dto

import ru.buhinder.alcopartyservice.entity.enums.EventType
import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class EventDto(
    val alcoholicsIds: Set<UUID>,

    @field:NotBlank(message = "title is required")
    @field:Size(min = 0, max = 50)
    val title: String,

    @field:NotBlank(message = "info is required")
    @field:Size(min = 0, max = 1000)
    val info: String,

    val photosIds: Set<UUID>,

    val type: EventType,

    @field:NotBlank(message = "location is required")
    @field:Size(min = 0, max = 200)
    val location: String,

    @field:NotNull(message = "startDate is required")
    val startDate: Long?,

    @field:NotNull(message = "endDate is required")
    val endDate: Long?,
)
