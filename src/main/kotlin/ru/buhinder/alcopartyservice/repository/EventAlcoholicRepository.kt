package ru.buhinder.alcopartyservice.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.EventAlcoholicEntity
import java.util.UUID

interface EventAlcoholicRepository : ReactiveCrudRepository<EventAlcoholicEntity, UUID> {

    fun findByEventIdAndAlcoholicIdAndIsBannedIsFalse(eventId: UUID, alcoholicId: UUID): Mono<EventAlcoholicEntity>

    fun findAllByEventId(eventId: UUID): Flux<EventAlcoholicEntity>

}
