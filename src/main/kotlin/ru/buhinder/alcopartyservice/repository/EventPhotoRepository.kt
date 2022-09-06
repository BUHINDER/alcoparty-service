package ru.buhinder.alcopartyservice.repository

import java.util.UUID
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.EventPhotoEntity

interface EventPhotoRepository : ReactiveCrudRepository<EventPhotoEntity, UUID> {

    fun findAllByEventId(eventId: UUID): Flux<EventPhotoEntity>

    fun findFirstByEventIdOrderByCreatedAtAsc(eventId: UUID): Mono<EventPhotoEntity>

}
