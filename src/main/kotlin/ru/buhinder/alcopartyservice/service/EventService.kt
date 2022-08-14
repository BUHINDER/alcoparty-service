package ru.buhinder.alcopartyservice.service

import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityNotFoundException
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.EventResponse
import ru.buhinder.alcopartyservice.dto.response.IdResponse
import ru.buhinder.alcopartyservice.repository.facade.EventDaoFacade
import ru.buhinder.alcopartyservice.service.strategy.EventStrategyRegistry
import java.util.UUID

@Service
class EventService(
    private val eventStrategyRegistry: EventStrategyRegistry,
    private val eventDaoFacade: EventDaoFacade,
    private val conversionService: ConversionService,
) {

    fun create(dto: EventDto, alcoholicId: UUID): Mono<IdResponse> {
        return eventStrategyRegistry.get(dto.type)
            .flatMap { it.create(dto, alcoholicId) }
    }

    fun join(eventId: UUID, alcoholicId: UUID): Mono<IdResponse> {
        return eventDaoFacade.getById(eventId)
            .flatMap { event ->
                if (event.createdBy == alcoholicId) {
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

    fun getAllEvents(alcoholicId: UUID): Flux<EventResponse> {
        return eventDaoFacade.findAllAndAlcoholicIsNotBanned(alcoholicId)
            .map { conversionService.convert(it, EventResponse::class.java)!! }
    }

    fun getEventById(eventId: UUID, alcoholicId: UUID): Mono<EventResponse> {
        return eventDaoFacade.findByIdAndAlcoholicIsNotBanned(eventId, alcoholicId)
            .map { conversionService.convert(it, EventResponse::class.java)!! }
            .switchIfEmpty {
                Mono.error(
                    EntityNotFoundException(
                        message = "Event not found",
                        payload = mapOf("id" to eventId)
                    )
                )
            }
    }

    fun findAllByAlcoholicId(alcoholicId: UUID): Mono<List<EventResponse>> {
        return eventDaoFacade.findAllByAlcoholicIdAndIsNotBanned(alcoholicId)
            .map { conversionService.convert(it, EventResponse::class.java)!! }
            .collectList()
    }

}
