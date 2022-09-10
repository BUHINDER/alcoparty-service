package ru.buhinder.alcopartyservice.repository.facade

import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.config.LoggerDelegate
import ru.buhinder.alcopartyservice.entity.EventPhotoEntity
import ru.buhinder.alcopartyservice.repository.EventPhotoRepository
import java.util.UUID

@Repository
class EventPhotoDaoFacade(
    private val r2dbcEntityOperations: R2dbcEntityOperations,
    private val eventPhotoRepository: EventPhotoRepository,
) {
    private val logger by LoggerDelegate()

    fun insert(eventPhotoEntity: EventPhotoEntity): Mono<EventPhotoEntity> {
        return r2dbcEntityOperations.insert(eventPhotoEntity)
    }

    fun insertAll(photos: List<EventPhotoEntity>): Mono<List<EventPhotoEntity>> {
        return eventPhotoRepository.saveAll(photos)
            .collectList()
            .map { it.toList() }
    }

    fun findAllByEventId(eventId: UUID): Flux<EventPhotoEntity> {
        return eventPhotoRepository.findAllByEventId(eventId)
    }

    fun findFirstByEventId(eventId: UUID): Mono<EventPhotoEntity> {
        return eventPhotoRepository.findFirstByEventIdOrderByCreatedAtAsc(eventId)
            .doOnNext { logger.info("Found photo for event $eventId") }
    }

}
