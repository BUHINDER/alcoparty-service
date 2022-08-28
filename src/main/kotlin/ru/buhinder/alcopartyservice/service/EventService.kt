package ru.buhinder.alcopartyservice.service

import org.springframework.core.convert.ConversionService
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ru.buhinder.alcopartyservice.controller.advice.exception.CannotJoinEventException
import ru.buhinder.alcopartyservice.controller.advice.exception.EntityNotFoundException
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.EventResponse
import ru.buhinder.alcopartyservice.dto.response.FullEventResponse
import ru.buhinder.alcopartyservice.dto.response.IdResponse
import ru.buhinder.alcopartyservice.entity.EventPhotoEntity
import ru.buhinder.alcopartyservice.entity.enums.PhotoType.ACTIVE
import ru.buhinder.alcopartyservice.repository.facade.EventDaoFacade
import ru.buhinder.alcopartyservice.repository.facade.EventPhotoDaoFacade
import ru.buhinder.alcopartyservice.service.strategy.EventStrategyRegistry
import ru.buhinder.alcopartyservice.service.validation.ImageValidationService
import java.util.UUID

@Service
class EventService(
    private val eventStrategyRegistry: EventStrategyRegistry,
    private val eventDaoFacade: EventDaoFacade,
    private val conversionService: ConversionService,
    private val minioService: MinioService,
    private val eventPhotoDaoFacade: EventPhotoDaoFacade,
    private val imageValidationService: ImageValidationService,
) {

    fun create(dto: EventDto, alcoholicId: UUID, images: List<FilePart>): Mono<FullEventResponse> {
        return imageValidationService.validateImageFormat(images)
            .flatMap { eventStrategyRegistry.get(dto.type) }
            .flatMap { it.create(dto, alcoholicId) }
            .flatMap { res ->
                minioService.saveEventImages(images)
                    .map { buildPhotosList(it, res.id) }
                    .flatMap { eventPhotoDaoFacade.insertAll(it) }
                    .flatMapIterable { it }
                    .map { it.photoId }
                    .collectList()
                    .map { FullEventResponse(res, it) }
            }
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

    fun getAllEvents(alcoholicId: UUID): Flux<FullEventResponse> {
        return eventDaoFacade.findAllAndAlcoholicIsNotBanned(alcoholicId)
            .map { conversionService.convert(it, EventResponse::class.java)!! }
            .flatMap { res ->
                eventPhotoDaoFacade.findAllByEventId(res.id)
                    .map { it.photoId }
                    .collectList()
                    .map { FullEventResponse(res, it) }
            }
    }

    fun getEventById(eventId: UUID, alcoholicId: UUID): Mono<FullEventResponse> {
        return eventDaoFacade.findByIdAndAlcoholicIsNotBanned(eventId, alcoholicId)
            .map { conversionService.convert(it, EventResponse::class.java)!! }
            .flatMap { res ->
                eventPhotoDaoFacade.findAllByEventId(res.id)
                    .map { it.photoId }
                    .collectList()
                    .map { FullEventResponse(res, it) }
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

    fun findAllByAlcoholicId(alcoholicId: UUID): Mono<List<EventResponse>> {
        return eventDaoFacade.findAllByAlcoholicIdAndIsNotBanned(alcoholicId)
            .map { conversionService.convert(it, EventResponse::class.java)!! }
            .collectList()
    }

    private fun buildPhotosList(photosIds: Set<UUID>, eventId: UUID): List<EventPhotoEntity> {
        return photosIds
            .map { EventPhotoEntity(eventId = eventId, photoId = it, type = ACTIVE) }
    }
}
