package ru.buhinder.alcopartyservice.repository.facade

import java.util.UUID
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityNotFoundException
import ru.buhinder.alcopartyservice.entity.InvitationLinkEntity
import ru.buhinder.alcopartyservice.repository.InvitationLinkRepository

@Repository
class InvitationLinkDaoFacade(
    private val invitationLinkRepository: InvitationLinkRepository,
) {

    fun insert(invitationLinkEntity: InvitationLinkEntity): Mono<InvitationLinkEntity> {
        return invitationLinkRepository.save(invitationLinkEntity)
    }

    fun getById(invitationLinkId: UUID): Mono<InvitationLinkEntity> {
        return invitationLinkRepository.findById(invitationLinkId)
            .switchIfEmpty {
                Mono.error(
                    EntityNotFoundException(
                        message = "Invitation link not found",
                        payload = mapOf("id" to invitationLinkId)
                    )
                )
            }
    }

    fun decrementUsageAmount(invitationLinkId: UUID): Mono<UUID> {
        return invitationLinkRepository.decrementUsageAmount(invitationLinkId)
            .map { invitationLinkId }
            .switchIfEmpty { invitationLinkId.toMono() }
    }

    fun getAllByEventId(eventId: UUID): Flux<InvitationLinkEntity> {
        return invitationLinkRepository.getAllByEventId(eventId)
    }

}
