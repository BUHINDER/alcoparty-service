package ru.buhinder.alcopartyservice.repository

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityNotFoundException
import ru.buhinder.alcopartyservice.entity.InvitationLinkEntity
import java.util.UUID

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
    }

}
