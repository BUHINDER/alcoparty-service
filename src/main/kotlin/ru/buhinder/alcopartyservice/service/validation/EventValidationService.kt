package ru.buhinder.alcopartyservice.service.validation

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.entity.enums.EventStatus.ENDED
import ru.buhinder.alcopartyservice.repository.EventDaoFacade
import java.util.UUID

@Service
class EventValidationService(
    private val eventDaoFacade: EventDaoFacade,
) {

    fun validateEventIsActive(eventId: UUID): Mono<UUID> {
        return eventDaoFacade.getById(eventId)
            .map {
                if (it.status == ENDED) {
                    throw CannotJoinEventException(
                        message = "Cannot join. Event has ended",
                        payload = mapOf("id" to eventId)
                    )
                }
                it.id!!
            }
    }

}
