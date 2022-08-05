package ru.buhinder.alcopartyservice.service.strategy

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.IdResponse
import ru.buhinder.alcopartyservice.entity.EventAlcoholicEntity
import ru.buhinder.alcopartyservice.entity.enums.EventType
import ru.buhinder.alcopartyservice.entity.enums.EventType.PUBLIC
import ru.buhinder.alcopartyservice.repository.EventAlcoholicDaoFacade
import ru.buhinder.alcopartyservice.service.validation.EventAlcoholicValidationService
import ru.buhinder.alcopartyservice.service.validation.EventValidationService
import java.util.UUID

@Component
class PublicEventStrategy(
    private val eventCreatorDelegate: EventCreatorDelegate,
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
    private val eventAlcoholicValidationService: EventAlcoholicValidationService,
    private val eventValidationService: EventValidationService,
) : EventStrategy {

    override fun create(dto: EventDto, alcoholicId: UUID): Mono<IdResponse> {
        return eventCreatorDelegate.create(dto, alcoholicId)
    }

    override fun join(eventId: UUID, alcoholicId: UUID): Mono<IdResponse> {
        return eventAlcoholicValidationService.validateAlcoholicIsNotAlreadyParticipating(eventId, alcoholicId)
            .flatMap { eventValidationService.validateEventIsActive(eventId) }
            .flatMap { eventAlcoholicDaoFacade.insert(EventAlcoholicEntity(UUID.randomUUID(), eventId, alcoholicId)) }
            .map { IdResponse(it.eventId) }
    }

    override fun getEventType(): EventType {
        return PUBLIC
    }

}
