package ru.buhinder.alcopartyservice.entity

import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("invitation_link")
open class InvitationLinkEntity(
    id: UUID? = null,
    val eventId: UUID,
    val usageAmount: Int = 1,
    val expiresAt: Long,
) : AbstractAuditable(id)
