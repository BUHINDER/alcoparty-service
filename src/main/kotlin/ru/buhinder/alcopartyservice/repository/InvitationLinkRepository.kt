package ru.buhinder.alcopartyservice.repository

import java.util.UUID
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.InvitationLinkEntity

interface InvitationLinkRepository : ReactiveCrudRepository<InvitationLinkEntity, UUID> {

    @Query(
        """
            update invitation_link
            set (usage_amount) = (select usage_amount - 1
                                  from invitation_link
                                  where id = :invitationLinkId)
            where id = :invitationLinkId
        """
    )
    fun decrementUsageAmount(invitationLinkId: UUID): Mono<Int>

    fun getAllByEventId(eventID: UUID): Flux<InvitationLinkEntity>

}
