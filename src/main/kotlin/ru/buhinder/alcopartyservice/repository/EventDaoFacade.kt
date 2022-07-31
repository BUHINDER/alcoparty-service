package ru.buhinder.alcopartyservice.repository

import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.EventEntity

@Repository
class EventDaoFacade(
    private val r2dbcEntityOperations: R2dbcEntityOperations,
) {

    fun insert(eventEntity: EventEntity): Mono<EventEntity> {
        return r2dbcEntityOperations.insert(eventEntity)
    }

}
