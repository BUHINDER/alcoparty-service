package ru.buhinder.alcopartyservice.dto

import java.util.UUID

data class InvitationLinkResponse(
    val id: UUID,
    val usageAmount: Int,
    val expiresAt: Long,
)
