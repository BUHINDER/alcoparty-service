package ru.buhinder.alcopartyservice.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.EventEntity
import java.util.UUID

interface EventRepository : ReactiveCrudRepository<EventEntity, UUID> {

    @Query(
        """
            select *
            from event ev
            where ev.id in (select event_id
                            from event_alcoholic
                            where alcoholic_id = :alcoholicId
                              and is_banned = false)
            union distinct
            select *
            from event ev
            where ev.id in (select e.id
                            from event e
                            except
                            select event_id
                            from event_alcoholic
                            where alcoholic_id = :alcoholicId
                              and is_banned)
            and ev.type != 'PRIVATE'
        """
    )
    fun findAllAndAlcoholicIsNotBanned(alcoholicId: UUID): Flux<EventEntity>

    @Query(
        """
            select *
            from event e
            where e.id = (select e.id
                            from event e
                            where e.id = :eventId
                            except
                            select event_id
                            from event_alcoholic
                            where alcoholic_id = :alcoholicId
                              and is_banned)
        """
    )
    fun findByIdAndAlcoholicIsNotBanned(eventId: UUID, alcoholicId: UUID): Mono<EventEntity>

    @Query(
        """
            select *
            from event e
            where e.id = (select e.id
                            from event e
                            where e.id = :eventId
                            except
                            select event_id
                            from event_alcoholic
                            where alcoholic_id = :alcoholicId
                              and is_banned)
                and e.status != 'ENDED'
        """
    )
    fun findByIdAndAlcoholicIsNotBannedAndStatusNotEnded(eventId: UUID, alcoholicId: UUID): Mono<EventEntity>

    @Query(
        """
            select e.*
            from event e
                join invitation_link il on e.id = il.event_id
            where il.id = :invitationLink
            and e.status != 'ENDED'
        """
    )
    fun findByInvitationLinkAndNotEnded(invitationLink: UUID): Mono<EventEntity>

    @Query(
        """
            select e.* from event e
                join event_alcoholic ea on e.id = ea.event_id
            where ea.alcoholic_id = :alcoholicId
            and ea.is_banned is false
        """
    )
    fun findAllByAlcoholicIdAndIsNotBanned(alcoholicId: UUID): Flux<EventEntity>
}
