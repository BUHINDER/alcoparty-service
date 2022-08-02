package ru.buhinder.alcopartyservice.service.validation

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.repository.EventAlcoholicDaoFacade
import java.util.UUID

@Service
class EventAlcoholicValidationService(
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
) {

    fun validateAlcoholicIsNotAlreadyParticipating(eventId: UUID, alcoholicId: UUID): Mono<Boolean> {
        return eventAlcoholicDaoFacade.findByEventIdAndAlcoholicId(eventId = eventId, alcoholicId = alcoholicId)
            .map {
                throw CannotJoinEventException(
                    message = "You are already participating in this event",
                    payload = mapOf("id" to it.eventId)
                )
                false
            }
            .switchIfEmpty { Mono.just(true) }
    }

}
