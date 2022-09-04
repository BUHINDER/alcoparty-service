package ru.buhinder.alcopartyservice.service.validation

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.entity.EventAlcoholicEntity
import ru.buhinder.alcopartyservice.repository.facade.EventAlcoholicDaoFacade
import java.util.UUID

@Service
class EventAlcoholicValidationService(
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
) {

    fun validateAlcoholicIsNotAlreadyParticipating(eventId: UUID, alcoholicId: UUID): Mono<Boolean> {
        return eventAlcoholicDaoFacade.findByEventIdAndAlcoholicId(eventId = eventId, alcoholicId = alcoholicId)
            .flatMap {
                Mono.error<Boolean>(
                    CannotJoinEventException(
                        message = "You are already participating in this event",
                        payload = mapOf("id" to it.eventId)
                    )
                )
            }
            .switchIfEmpty { Mono.just(true) }
    }


    fun validateAlcoholicIsParticipatingInTheEvent(eventId: UUID, alcoholicId: UUID): Mono<EventAlcoholicEntity> {
        return eventAlcoholicDaoFacade.findByEventIdAndAlcoholicId(eventId = eventId, alcoholicId = alcoholicId)
            .map { it }
            .switchIfEmpty {
                Mono.error(
                    CannotJoinEventException(
                        message = "You are not participating in this event",
                        payload = mapOf("id" to eventId)
                    )
                )
            }
    }

}
