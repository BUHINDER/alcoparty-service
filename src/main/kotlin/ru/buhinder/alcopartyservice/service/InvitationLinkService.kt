package ru.buhinder.alcopartyservice.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.dto.response.IdResponse
import ru.buhinder.alcopartyservice.entity.EventAlcoholicEntity
import ru.buhinder.alcopartyservice.entity.EventEntity
import ru.buhinder.alcopartyservice.entity.InvitationLinkEntity
import ru.buhinder.alcopartyservice.repository.facade.EventAlcoholicDaoFacade
import ru.buhinder.alcopartyservice.repository.facade.EventDaoFacade
import ru.buhinder.alcopartyservice.repository.facade.InvitationLinkDaoFacade
import ru.buhinder.alcopartyservice.service.validation.EventAlcoholicValidationService
import ru.buhinder.alcopartyservice.service.validation.InvitationLinkValidationService
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class InvitationLinkService(
    private val invitationLinkValidationService: InvitationLinkValidationService,
    private val eventAlcoholicValidationService: EventAlcoholicValidationService,
    private val invitationLinkDaoFacade: InvitationLinkDaoFacade,
    private val eventDaoFacade: EventDaoFacade,
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
) {

    fun create(eventId: UUID, alcoholicId: UUID): Mono<IdResponse> {
        val expiresAt = Instant.now().plusSeconds(Duration.ofDays(1).toSeconds()).toEpochMilli()
        return invitationLinkValidationService.validateCanBeCreated(eventId = eventId, alcoholicId = alcoholicId)
            .map { InvitationLinkEntity(eventId = eventId, expiresAt = expiresAt, createdBy = alcoholicId) }
            .flatMap { invitationLinkDaoFacade.insert(it) }
            .map { IdResponse(it.id!!) }
    }

    fun join(invitationLink: UUID, alcoholicId: UUID): Mono<IdResponse> {
        return invitationLinkValidationService.validateUsageAmountAndNotExpired(invitationLink)
            .flatMap { eventDaoFacade.getByInvitationLinkAndNotEnded(invitationLink) }
            .flatMap { event ->
                validate(event, alcoholicId)
                    .flatMap { eventAlcoholicValidationService.validateAlcoholicIsNotBanned(it, alcoholicId) }
                    .flatMap { eventAlcoholicValidationService.validateAlcoholicIsNotAlreadyParticipating(it, alcoholicId) }
                    .flatMap { eventAlcoholicDaoFacade.insert(EventAlcoholicEntity(UUID.randomUUID(), it, alcoholicId)) }
                    .flatMap { invitationLinkDaoFacade.decrementUsageAmount(invitationLink) }
                    .map { IdResponse(event.id!!) }
            }
    }

    private fun validate(event: EventEntity, alcoholicId: UUID): Mono<UUID> {
        return event.toMono()
            .map {
                if (event.createdBy == alcoholicId) {
                    throw CannotJoinEventException(
                        message = "You cannot join your own event",
                        payload = mapOf("id" to event.id!!)
                    )
                }
                event.id!!
            }
    }
}
