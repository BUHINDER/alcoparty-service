package ru.buhinder.alcopartyservice.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import ru.buhinder.alcopartyservice.entity.EventPhotoEntity
import java.util.UUID

interface EventPhotoRepository : ReactiveCrudRepository<EventPhotoEntity, UUID> {

    fun findAllByEventId(eventId: UUID): Flux<EventPhotoEntity>

}
