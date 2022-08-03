package ru.buhinder.alcopartyservice.service.validation

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityCannotBeCreatedException
import ru.buhinder.alcopartyservice.entity.enums.EventType.PRIVATE
import ru.buhinder.alcopartyservice.repository.EventAlcoholicDaoFacade
import ru.buhinder.alcopartyservice.repository.EventDaoFacade
import java.util.UUID

@Service
class InvitationLinkValidationService(
    private val eventDaoFacade: EventDaoFacade,
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
) {

    // TODO: 03/08/2022 must be refactored
    fun validate(eventId: UUID, alcoholicId: UUID): Mono<Boolean> {
        return eventDaoFacade.findByIdAndAlcoholicIsNotBanned(eventId = eventId, alcoholicId = alcoholicId)
            .flatMap { entity ->
                if (entity.type == PRIVATE) {
                    if (entity.eventCreator != alcoholicId) {
                        return@flatMap Mono.error(
                            EntityCannotBeCreatedException(
                                message = "Only event creator is allowed to create invitation links for a private event",
                                payload = emptyMap()
                            )
                        )
                    }
                    true.toMono()
                } else {
                    eventAlcoholicDaoFacade.findByEventIdAndAlcoholicIdAndIsBannedIsFalse(
                        eventId = eventId,
                        alcoholicId = alcoholicId
                    )
                        .map { true }
                        .switchIfEmpty {
                            Mono.error(
                                EntityCannotBeCreatedException(
                                    message = "Only active event participants are allowed to create invitation links",
                                    payload = emptyMap()
                                )
                            )
                        }
                }
            }
    }

}
