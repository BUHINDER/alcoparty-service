package ru.buhinder.alcopartyservice.service.validation

import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.util.function.Tuple2
import ru.buhinder.alcopartyservice.config.LoggerDelegate
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityCannotBeCreatedException
import ru.buhinder.alcopartyservice.controller.advice.exception.InsufficientPermissionException
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.entity.enums.EventStatus.ENDED
import ru.buhinder.alcopartyservice.repository.facade.EventDaoFacade

@Service
class EventValidationService(
    private val eventDaoFacade: EventDaoFacade,
) {
    private val logger by LoggerDelegate()

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

    fun validateIsEventCreator(eventIdAlcoholicIdTuple: Tuple2<UUID, UUID>): Mono<Tuple2<UUID, UUID>> {
        return eventDaoFacade.getById(eventIdAlcoholicIdTuple.t1)
            .filter { it.createdBy != eventIdAlcoholicIdTuple.t2 }
            .flatMap {
                Mono.error<Tuple2<UUID, UUID>>(
                    InsufficientPermissionException(
                        message = "Alcoholic with id ${eventIdAlcoholicIdTuple.t2} doesn't have permission to manage event",
                        payload = emptyMap()
                    )
                )
            }
            .switchIfEmpty { eventIdAlcoholicIdTuple.toMono() }
    }
}
