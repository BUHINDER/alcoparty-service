package ru.buhinder.alcopartyservice.service

import java.util.UUID
import org.springframework.core.convert.ConversionService
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityNotFoundException
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.EventResponse
import ru.buhinder.alcopartyservice.dto.response.IdResponse
import ru.buhinder.alcopartyservice.dto.response.MultipleEventResponse
import ru.buhinder.alcopartyservice.dto.response.PageableResponse
import ru.buhinder.alcopartyservice.dto.response.SingleEventResponse
import ru.buhinder.alcopartyservice.entity.EventPhotoEntity
import ru.buhinder.alcopartyservice.entity.enums.PhotoType.ACTIVE
import ru.buhinder.alcopartyservice.repository.facade.EventAlcoholicDaoFacade
import ru.buhinder.alcopartyservice.repository.facade.EventDaoFacade
import ru.buhinder.alcopartyservice.repository.facade.EventPhotoDaoFacade
import ru.buhinder.alcopartyservice.service.strategy.EventStrategyRegistry
import ru.buhinder.alcopartyservice.service.validation.EventAlcoholicValidationService
import ru.buhinder.alcopartyservice.service.validation.ImageValidationService

@Service
class EventService(
    private val eventStrategyRegistry: EventStrategyRegistry,
    private val eventDaoFacade: EventDaoFacade,
    private val conversionService: ConversionService,
    private val minioService: MinioService,
    private val eventPhotoDaoFacade: EventPhotoDaoFacade,
    private val imageValidationService: ImageValidationService,
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
    private val eventAlcoholicValidationService: EventAlcoholicValidationService,
    private val paginationService: PaginationService
) {

    fun create(dto: EventDto, alcoholicId: UUID, images: List<FilePart>): Mono<IdResponse> {
        return imageValidationService.validateImageFormat(images)
            .flatMap { eventStrategyRegistry.get(dto.type) }
            .flatMap { it.create(dto, alcoholicId) }
            .flatMap { res ->
                val eventId = res.id
                minioService.saveEventImages(images)
                    .map { buildPhotosList(it, eventId) }
                    .flatMap { eventPhotoDaoFacade.insertAll(it) }
                    .map { IdResponse(eventId) }
            }
    }

    fun join(eventId: UUID, alcoholicId: UUID): Mono<IdResponse> {
        return eventAlcoholicValidationService.validateAlcoholicIsNotBanned(eventId, alcoholicId)
            .flatMap { eventDaoFacade.getById(eventId) }
            .flatMap { event ->
                if (event.createdBy == alcoholicId) {
                    Mono.error(
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

    fun leave(eventId: UUID, alcoholicId: UUID): Mono<Void> {
        return eventAlcoholicValidationService.validateAlcoholicIsAParticipant(eventId, alcoholicId)
            .flatMap { eventAlcoholicDaoFacade.findByEventIdAndAlcoholicId(eventId, alcoholicId) }
            .flatMap { eventAlcoholicDaoFacade.delete(it) }
    }

    fun disband(eventId: UUID, currentAlcoholicId: UUID): Mono<Void> {
        return eventAlcoholicValidationService.validateUserIsTheEventOwner(eventId, currentAlcoholicId)
            .flatMap { eventDaoFacade.deleteById(eventId) }
    }

    fun getAllEvents(alcoholicId: UUID, page: Int, pageSize: Int): Mono<PageableResponse<MultipleEventResponse>> {
        return eventDaoFacade.findAllAndAlcoholicIsNotBanned(alcoholicId, page, pageSize)
            .map { conversionService.convert(it, EventResponse::class.java)!! }
            .flatMap { res ->
                val eventId = res.id
                eventAlcoholicDaoFacade.findAllByEventId(eventId).any { it.alcoholicId == alcoholicId }
                    .flatMap { isParticipant ->
                        eventPhotoDaoFacade.findFirstByEventId(alcoholicId).map { it.photoId }
                            .map { MultipleEventResponse(res, it, isParticipant) }
                            .switchIfEmpty(MultipleEventResponse(res, null, isParticipant).toMono())
                    }
            }
            .collectList()
            .switchIfEmpty(emptyList<MultipleEventResponse>().toMono())
            .zipWith(eventDaoFacade.countAllAndAlcoholicIsNotBanned(alcoholicId))
            .map { allEventsResponse ->
                val pagination = paginationService.createPagination(allEventsResponse.t2, page, pageSize)
                PageableResponse(allEventsResponse.t1, pagination)
            }
    }

    fun getEventById(eventId: UUID, alcoholicId: UUID): Mono<SingleEventResponse> {
        return eventDaoFacade.findByIdAndAlcoholicIsNotBanned(eventId, alcoholicId)
            .map { conversionService.convert(it, EventResponse::class.java)!! }
            .flatMap { res ->
                eventPhotoDaoFacade.findAllByEventId(eventId)
                    .map { it.photoId }
                    .collectList()
                    .flatMap { photos ->
                        eventAlcoholicDaoFacade.findAllByEventId(eventId)
                            .map { it.alcoholicId }
                            .collectList()
                            .map { SingleEventResponse(res, photos, it) }
                    }
            }
            .switchIfEmpty {
                Mono.error(
                    EntityNotFoundException(
                        message = "Event not found",
                        payload = mapOf("id" to eventId)
                    )
                )
            }
    }

    fun findAllByAlcoholicId(alcoholicId: UUID, page: Int, pageSize: Int): Mono<PageableResponse<EventResponse>> {

        return eventDaoFacade.findAllByAlcoholicIdAndIsNotBanned(alcoholicId, page, pageSize)
            .map { conversionService.convert(it, EventResponse::class.java)!! }
            .collectList()
            .zipWith(eventDaoFacade.countAllByAlcoholicIdAndIsNotBanned(alcoholicId))
            .map { events ->
                val pagination = paginationService.createPagination(events.t2, page, pageSize)
                PageableResponse(events.t1, pagination)
            }
    }

    private fun buildPhotosList(photosIds: Set<UUID>, eventId: UUID): List<EventPhotoEntity> {
        return photosIds
            .map { EventPhotoEntity(eventId = eventId, photoId = it, type = ACTIVE) }
    }

}
