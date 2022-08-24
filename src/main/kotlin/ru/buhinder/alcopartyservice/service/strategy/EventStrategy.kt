package ru.buhinder.alcopartyservice.service.strategy

import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.EventResponse
import ru.buhinder.alcopartyservice.dto.response.IdResponse
import ru.buhinder.alcopartyservice.entity.enums.EventType
import java.util.UUID

interface EventStrategy {

    fun create(dto: EventDto, alcoholicId: UUID): Mono<EventResponse>

    fun join(eventId: UUID, alcoholicId: UUID): Mono<IdResponse>

    fun getEventType(): EventType

}
