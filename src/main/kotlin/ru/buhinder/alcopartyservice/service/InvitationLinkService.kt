package ru.buhinder.alcopartyservice.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.InvitationLinkEntity
import ru.buhinder.alcopartyservice.repository.InvitationLinkDaoFacade
import ru.buhinder.alcopartyservice.service.validation.InvitationLinkValidationService
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class InvitationLinkService(
    private val invitationLinkValidationService: InvitationLinkValidationService,
    private val invitationLinkDaoFacade: InvitationLinkDaoFacade,
) {

    fun create(eventId: UUID, alcoholicId: UUID): Mono<UUID> {
        val expiresAt = Instant.now().plusSeconds(Duration.ofDays(1).toSeconds()).toEpochMilli()
        return invitationLinkValidationService.validate(eventId = eventId, alcoholicId = alcoholicId)
            .map { InvitationLinkEntity(eventId = eventId, expiresAt = expiresAt) }
            .flatMap { invitationLinkDaoFacade.insert(it) }
            .map { it.id!! }
    }

}
