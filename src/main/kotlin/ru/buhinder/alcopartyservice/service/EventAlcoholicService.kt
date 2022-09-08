package ru.buhinder.alcopartyservice.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.EventAlcoholicEntity
import ru.buhinder.alcopartyservice.repository.facade.EventAlcoholicDaoFacade
import ru.buhinder.alcopartyservice.service.validation.EventAlcoholicValidationService
import java.util.UUID

@Service
class EventAlcoholicService(
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
    private val eventAlcoholicValidationService: EventAlcoholicValidationService,
) {

    fun block(eventId: UUID, alcoholicId: UUID, currentAlcoholicId: UUID): Mono<Boolean> {
        return eventAlcoholicValidationService.validateUserIsTheEventOwner(eventId, currentAlcoholicId)
            .flatMap { eventAlcoholicValidationService.validateAlcoholicIsAParticipant(eventId, alcoholicId) }
            .flatMap { eventAlcoholicDaoFacade.findByEventIdAndAlcoholicId(eventId, alcoholicId) }
            .flatMap { eventAlcoholicDaoFacade.update(updateEventAlcoholicEntity(it)) }
            .map { true }
    }

    private fun updateEventAlcoholicEntity(it: EventAlcoholicEntity): EventAlcoholicEntity {
        return EventAlcoholicEntity(
            it.id,
            it.eventId,
            it.alcoholicId,
            true,
            it.createdAt,
            it.version,
        )
    }

}
