package ru.buhinder.alcopartyservice.repository.facade

import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.CriteriaDefinition
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.config.LoggerDelegate
import ru.buhinder.alcopartyservice.entity.EventAlcoholicEntity
import ru.buhinder.alcopartyservice.repository.EventAlcoholicRepository
import java.util.UUID

@Repository
class EventAlcoholicDaoFacade(
    private val r2dbcEntityOperations: R2dbcEntityOperations,
    private val eventAlcoholicRepository: EventAlcoholicRepository,
) {
    private val logger by LoggerDelegate()

    fun insert(eventAlcoholicEntity: EventAlcoholicEntity): Mono<EventAlcoholicEntity> {
        return r2dbcEntityOperations.insert(eventAlcoholicEntity)
    }

    fun insertAll(alcoholics: List<EventAlcoholicEntity>): Mono<List<EventAlcoholicEntity>> {
        return eventAlcoholicRepository.saveAll(alcoholics)
            .collectList()
            .map { it.toList() }
    }

    fun findByEventIdAndAlcoholicId(eventId: UUID, alcoholicId: UUID): Mono<EventAlcoholicEntity> {
        return Mono.just(logger.info("Trying to find alcoholic with id $alcoholicId for event with id $eventId"))
            .flatMap {
                r2dbcEntityOperations.selectOne(
                    Query.query(
                        CriteriaDefinition.from(
                            Criteria.where("event_id").`is`(eventId),
                            Criteria.where("alcoholic_id").`is`(alcoholicId)
                        )
                    ),
                    EventAlcoholicEntity::class.java
                )
            }
            .doOnNext { logger.info("Found alcoholic with id $alcoholicId for event with id $eventId") }
            .doOnError { logger.info("Error retrieving alcoholic with id $alcoholicId for event with id $eventId") }
    }

    fun findByEventIdAndAlcoholicIdAndIsBannedIsFalse(eventId: UUID, alcoholicId: UUID): Mono<EventAlcoholicEntity> {
        return eventAlcoholicRepository.findByEventIdAndAlcoholicIdAndIsBannedIsFalse(
            eventId = eventId,
            alcoholicId = alcoholicId
        )
    }

}
