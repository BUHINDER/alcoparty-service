package ru.buhinder.alcopartyservice.entity

import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("event_alcoholic")
open class EventAlcoholicEntity(
    id: UUID? = null,
    val eventId: UUID,
    val alcoholicId: UUID,
    val isBanned: Boolean? = false,
    val createdAt: Long? = Instant.now().toEpochMilli(),
) : AbstractAuditable(id)
