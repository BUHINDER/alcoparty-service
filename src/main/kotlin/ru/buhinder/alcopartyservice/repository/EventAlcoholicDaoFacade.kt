package ru.buhinder.alcopartyservice.repository

import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.EventAlcoholicEntity

@Repository
class EventAlcoholicDaoFacade(
    private val r2dbcEntityOperations: R2dbcEntityOperations,
    private val eventAlcoholicReactiveCrudRepository: EventAlcoholicReactiveCrudRepository,
) {

    fun insert(eventAlcoholicEntity: EventAlcoholicEntity): Mono<EventAlcoholicEntity> {
        return r2dbcEntityOperations.insert(eventAlcoholicEntity)
    }

    fun insertAll(alcoholics: List<EventAlcoholicEntity>): Mono<List<EventAlcoholicEntity>> {
        return eventAlcoholicReactiveCrudRepository.saveAll(alcoholics)
            .collectList()
            .map { it.toList() }
    }

}
