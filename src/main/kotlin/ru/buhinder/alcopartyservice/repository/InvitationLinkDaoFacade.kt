package ru.buhinder.alcopartyservice.repository

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.InvitationLinkEntity

@Repository
class InvitationLinkDaoFacade(
    private val invitationLinkRepository: InvitationLinkRepository,
) {

    fun insert(invitationLinkEntity: InvitationLinkEntity): Mono<InvitationLinkEntity> {
        return invitationLinkRepository.save(invitationLinkEntity)
    }

}
