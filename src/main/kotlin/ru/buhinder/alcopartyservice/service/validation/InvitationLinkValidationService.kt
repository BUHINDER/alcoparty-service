package ru.buhinder.alcopartyservice.service.validation

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityCannotBeCreatedException
import ru.buhinder.alcopartyservice.entity.enums.EventType.PRIVATE
import ru.buhinder.alcopartyservice.repository.facade.EventAlcoholicDaoFacade
import ru.buhinder.alcopartyservice.repository.facade.EventDaoFacade
import ru.buhinder.alcopartyservice.repository.facade.InvitationLinkDaoFacade
import java.time.Instant
import java.util.UUID

@Service
class InvitationLinkValidationService(
    private val eventDaoFacade: EventDaoFacade,
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
    private val invitationLinkDaoFacade: InvitationLinkDaoFacade,
) {

    // TODO: 03/08/2022 must be refactored
    fun validateCanBeCreated(eventId: UUID, alcoholicId: UUID): Mono<Boolean> {
        return eventDaoFacade.getByIdAndAlcoholicIsNotBannedAndStatusNotEnded(
            eventId = eventId,
            alcoholicId = alcoholicId
        )
            .flatMap { entity ->
                if (entity.type == PRIVATE) {
                    if (entity.createdBy != alcoholicId) {
                        return@flatMap Mono.error(
                            EntityCannotBeCreatedException(
                                message = "Only event creator is allowed to create invitation links for a private event",
                                payload = emptyMap()
                            )
                        )
                    }
                    true.toMono()
                } else {
                    eventAlcoholicDaoFacade.findByEventIdAndAlcoholicIdAndIsBannedIsFalse(
                        eventId = eventId,
                        alcoholicId = alcoholicId
                    )
                        .map { true }
                        .switchIfEmpty {
                            Mono.error(
                                EntityCannotBeCreatedException(
                                    message = "Only active event participants are allowed to create invitation links",
                                    payload = emptyMap()
                                )
                            )
                        }
                }
            }
    }

    fun validateUsageAmountAndNotExpired(invitationLink: UUID): Mono<UUID> {
        return invitationLinkDaoFacade.getById(invitationLink)
            .map {
                if (it.usageAmount < 1) {
                    throw CannotJoinEventException(
                        message = "Insufficient usages left",
                        payload = emptyMap()
                    )
                }
                if (Instant.now().toEpochMilli() > it.expiresAt) {
                    throw CannotJoinEventException(
                        message = "Invitation link has expired",
                        payload = emptyMap()
                    )
                }
                invitationLink
            }
    }

}
