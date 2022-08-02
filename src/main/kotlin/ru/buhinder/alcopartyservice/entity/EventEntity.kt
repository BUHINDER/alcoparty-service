package ru.buhinder.alcopartyservice.entity

import org.springframework.data.relational.core.mapping.Table
import ru.buhinder.alcopartyservice.entity.enums.EventStatus
import ru.buhinder.alcopartyservice.entity.enums.EventType
import java.util.UUID

@Table("event")
open class EventEntity(
    id: UUID? = null,
    val info: String,
    val type: EventType,
    val location: String,
    val status: EventStatus,
    val startDate: Long,
    val endDate: Long? = null,
    val eventCreator: UUID,
) : AbstractAuditable(id)
