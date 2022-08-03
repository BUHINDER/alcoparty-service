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
    fun findAllNotPrivateAndAlcoholicIsNotBanned(alcoholicId: UUID): Flux<EventEntity>

    @Query(
        """
            select *
            from event e
            where e.id = (select e.id
                            from event e
                            where e.id = :eventId
                              and e.type != 'PRIVATE'
                            except
                            select event_id
                            from event_alcoholic
                            where alcoholic_id = :alcoholicId
                              and is_banned)
        """
    )
    fun findByIdAndNotPrivateAndAlcoholicIsNotBanned(eventId: UUID, alcoholicId: UUID): Mono<EventEntity>

}
