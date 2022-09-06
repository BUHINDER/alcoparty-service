package ru.buhinder.alcopartyservice.repository

import java.util.UUID
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.EventEntity

interface EventRepository : ReactiveCrudRepository<EventEntity, UUID> {

    @Query(
        """
            select *, start_date - EXTRACT(epoch from now()) * 1000 as start_ev_diff
            from event ev
            where ev.id in (select event_id
                            from event_alcoholic
                            where alcoholic_id = :alcoholicId
                              and is_banned = false)
            union distinct
            select *, start_date - EXTRACT(epoch from now()) * 1000 as start_ev_diff
            from event ev
            where ev.id in (select e.id
                            from event e
                            except
                            select event_id
                            from event_alcoholic
                            where alcoholic_id = :alcoholicId
                              and is_banned)
              and ev.type != 'PRIVATE'
            order by start_ev_diff
            offset :page * :pageSize limit :pageSize
        """
    )
    fun findAllAndAlcoholicIsNotBanned(alcoholicId: UUID, page: Int, pageSize: Int): Flux<EventEntity>

    @Query(
        """
            select count(*)
            from event ev
            where ev.id in (select event_id
                            from event_alcoholic
                            where alcoholic_id = :alcoholicId
                              and is_banned = false)
            union distinct
            select count(*)
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
    fun countAllAndAlcoholicIsNotBanned(alcoholicId: UUID): Mono<Long>

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
            select e.*, start_date - EXTRACT(epoch from now()) * 1000 as start_ev_diff from event e
                join event_alcoholic ea on e.id = ea.event_id
            where ea.alcoholic_id = :alcoholicId
            and ea.is_banned is false
            order by start_ev_diff
            offset :page * :pageSize limit :pageSize
        """,
    )
    fun findAllByAlcoholicIdAndIsNotBanned(alcoholicId: UUID, page: Int, pageSize: Int): Flux<EventEntity>

    @Query(
        """
            select count(e.*) from event e
                join event_alcoholic ea on e.id = ea.event_id
            where ea.alcoholic_id = :alcoholicId
            and ea.is_banned is false
        """,
    )
    fun countAllByAlcoholicIdAndIsNotBanned(alcoholicId: UUID): Mono<Long>
}
