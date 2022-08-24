package ru.buhinder.alcopartyservice.service.validation

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityCannotBeCreatedException
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.entity.enums.EventStatus.ENDED
import ru.buhinder.alcopartyservice.repository.facade.EventDaoFacade
import java.time.Instant
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

    fun validateDates(dto: EventDto): Mono<EventDto> {
        val startDate = dto.startDate!!
        val endDate = dto.endDate!!
        val now = Instant.now().toEpochMilli()

        if (startDate >= endDate) {
            return Mono.error {
                EntityCannotBeCreatedException(
                    message = "Event start date and time must be earlier than the end date and time",
                    payload = emptyMap()
                )
            }
        }

        if (endDate <= now) {
            return Mono.error {
                EntityCannotBeCreatedException(
                    message = "Event end date and time must be greater than the current date and time",
                    payload = emptyMap()
                )
            }
        }
        return dto.toMono()
    }

}
