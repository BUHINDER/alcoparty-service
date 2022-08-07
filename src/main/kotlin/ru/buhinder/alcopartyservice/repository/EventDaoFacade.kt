package ru.buhinder.alcopartyservice.repository

import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityNotFoundException
import ru.buhinder.alcopartyservice.entity.EventEntity
import java.util.UUID

@Repository
class EventDaoFacade(
    private val r2dbcEntityOperations: R2dbcEntityOperations,
    private val eventRepository: EventRepository,
) {
    companion object {
        const val NOT_FOUND_MESSAGE = "Event not found"
    }

    fun insert(eventEntity: EventEntity): Mono<EventEntity> {
        return r2dbcEntityOperations.insert(eventEntity)
    }

    fun getById(eventId: UUID): Mono<EventEntity> {
        return r2dbcEntityOperations.selectOne(
            Query.query(Criteria.where("id").`is`(eventId)),
            EventEntity::class.java
        )
            .switchIfEmpty {
                Mono.error(
                    EntityNotFoundException(
                        message = NOT_FOUND_MESSAGE,
                        payload = mapOf("id" to eventId)
                    )
                )
            }
    }

    fun findAllNotPrivateAndAlcoholicIsNotBanned(alcoholicId: UUID): Flux<EventEntity> {
        return eventRepository.findAllNotPrivateAndAlcoholicIsNotBanned(alcoholicId)
    }

    fun findByIdAndNotPrivateAndAlcoholicIsNotBanned(eventId: UUID, alcoholicId: UUID): Mono<EventEntity> {
        return eventRepository.findByIdAndNotPrivateAndAlcoholicIsNotBanned(eventId, alcoholicId)
    }

    fun getByIdAndAlcoholicIsNotBannedAndStatusNotEnded(eventId: UUID, alcoholicId: UUID): Mono<EventEntity> {
        return eventRepository.findByIdAndAlcoholicIsNotBannedAndStatusNotEnded(eventId, alcoholicId)
            .switchIfEmpty {
                Mono.error(
                    EntityNotFoundException(
                        message = NOT_FOUND_MESSAGE,
                        payload = mapOf("id" to eventId)
                    )
                )
            }
    }

    fun getByInvitationLinkAndNotEnded(invitationLink: UUID): Mono<EventEntity> {
        return eventRepository.findByInvitationLinkAndNotEnded(invitationLink)
            .switchIfEmpty {
                Mono.error(
                    EntityNotFoundException(
                        message = NOT_FOUND_MESSAGE,
                        payload = emptyMap()
                    )
                )
            }
    }

}
