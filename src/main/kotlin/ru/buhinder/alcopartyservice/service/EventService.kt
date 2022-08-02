package ru.buhinder.alcopartyservice.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.EventResponse
import ru.buhinder.alcopartyservice.repository.EventDaoFacade
import ru.buhinder.alcopartyservice.service.strategy.EventStrategyRegistry
import java.util.UUID

@Service
class EventService(
    private val eventStrategyRegistry: EventStrategyRegistry,
    private val eventDaoFacade: EventDaoFacade,
) {

    fun create(dto: EventDto, eventCreator: UUID): Mono<EventResponse> {
        return eventStrategyRegistry.get(dto.type)
            .flatMap { it.create(dto, eventCreator) }
    }

    fun join(eventId: UUID, alcoholicId: UUID): Mono<UUID> {
        return eventDaoFacade.getById(eventId)
            .flatMap { event ->
                if (event.eventCreator == alcoholicId) {
                    return@flatMap Mono.error(
                        CannotJoinEventException(
                            message = "You cannot join your own event",
                            payload = mapOf("id" to eventId)
                        )
                    )
                } else {
                    eventStrategyRegistry.get(event.type)
                        .flatMap { it.join(eventId = eventId, alcoholicId = alcoholicId) }
                }
            }
    }

}
