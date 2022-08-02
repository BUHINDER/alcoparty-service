package ru.buhinder.alcopartyservice.service

import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.EventResponse
import ru.buhinder.alcopartyservice.entity.EventAlcoholicEntity
import ru.buhinder.alcopartyservice.entity.EventEntity
import ru.buhinder.alcopartyservice.entity.EventPhotoEntity
import ru.buhinder.alcopartyservice.entity.enums.PhotoType.ACTIVE
import ru.buhinder.alcopartyservice.model.EventModel
import ru.buhinder.alcopartyservice.repository.EventAlcoholicDaoFacade
import ru.buhinder.alcopartyservice.repository.EventDaoFacade
import ru.buhinder.alcopartyservice.repository.EventPhotoDaoFacade
import java.util.UUID

@Service
class EventService(
    private val conversionService: ConversionService,
    private val eventDaoFacade: EventDaoFacade,
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
    private val eventPhotoDaoFacade: EventPhotoDaoFacade,
) {

    fun create(dto: EventDto, eventCreator: UUID): Mono<EventResponse> {
        val eventModel = EventModel(dto, eventCreator)
        val entity = conversionService.convert(eventModel, EventEntity::class.java)!!
        val alcoholics = dto.alcoholicsIds.map { EventAlcoholicEntity(eventId = entity.id!!, alcoholicId = it) }
        val photos = dto.photosIds.map { EventPhotoEntity(eventId = entity.id!!, photoId = it, type = ACTIVE) }
        return entity.toMono()
            .flatMap { savedEntity ->
                eventDaoFacade.insert(savedEntity)
                    .flatMap { eventAlcoholicDaoFacade.insertAll(alcoholics) }
                    .flatMap { eventPhotoDaoFacade.insertAll(photos) }
                    .map { savedEntity }
            }
            .map { EventResponse(it.id!!) }
    }

}
