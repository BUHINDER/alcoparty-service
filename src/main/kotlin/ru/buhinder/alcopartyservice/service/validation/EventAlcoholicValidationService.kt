package ru.buhinder.alcopartyservice.service.validation

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityCannotBeUpdatedException
import ru.buhinder.alcopartyservice.controller.advice.exception.InsufficientPermissionException
import ru.buhinder.alcopartyservice.repository.facade.EventAlcoholicDaoFacade
import ru.buhinder.alcopartyservice.repository.facade.EventDaoFacade
import java.util.UUID

@Service
class EventAlcoholicValidationService(
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
    private val eventDaoFacade: EventDaoFacade,
) {

    fun validateAlcoholicIsNotAlreadyParticipating(eventId: UUID, alcoholicId: UUID): Mono<Boolean> {
        return eventAlcoholicDaoFacade.findByEventIdAndAlcoholicId(eventId = eventId, alcoholicId = alcoholicId)
            .flatMap {
                Mono.error<Boolean>(
                    CannotJoinEventException(
                        message = "You are already participating in this event",
                        payload = mapOf("id" to eventId)
                    )
                )
            }
            .switchIfEmpty { Mono.just(true) }
    }

    fun validateAlcoholicIsNotBanned(eventId: UUID, alcoholicId: UUID): Mono<Boolean> {
        return eventAlcoholicDaoFacade.findByEventIdAndAlcoholicId(eventId = eventId, alcoholicId = alcoholicId)
            .map {
                if (it.isBanned) {
                    throw CannotJoinEventException(
                        message = "You were banned from this event",
                        payload = mapOf("id" to eventId)
                    )
                }
                true
            }
            .switchIfEmpty { Mono.just(true) }
    }

    fun validateAlcoholicIsAParticipant(eventId: UUID, alcoholicId: UUID): Mono<Boolean> {
        return eventAlcoholicDaoFacade.findByEventIdAndAlcoholicId(eventId = eventId, alcoholicId = alcoholicId)
            .map {
                if (it.isBanned) {
                    throw EntityCannotBeUpdatedException(
                        message = "User was banned from this event",
                        payload = mapOf("id" to eventId)
                    )
                }
                true
            }
            .switchIfEmpty {
                Mono.error(
                    EntityCannotBeUpdatedException(
                        message = "User is not participating in this event",
                        payload = mapOf("id" to eventId)
                    )
                )
            }
    }

    fun validateUserIsTheEventOwner(eventId: UUID, alcoholicId: UUID): Mono<Boolean> {
        return eventDaoFacade.getById(eventId)
            .map {
                if (it.createdBy != alcoholicId) {
                    throw InsufficientPermissionException(
                        message = "Insufficient permission. Must be the event owner",
                        payload = emptyMap()
                    )
                }
                true
            }
    }

}
