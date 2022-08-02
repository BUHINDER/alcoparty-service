package ru.buhinder.alcopartyservice.service.strategy

import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.IdResponse
import ru.buhinder.alcopartyservice.entity.EventAlcoholicEntity
import ru.buhinder.alcopartyservice.entity.EventEntity
import ru.buhinder.alcopartyservice.entity.EventPhotoEntity
import ru.buhinder.alcopartyservice.entity.enums.PhotoType
import ru.buhinder.alcopartyservice.model.EventModel
import ru.buhinder.alcopartyservice.repository.EventAlcoholicDaoFacade
import ru.buhinder.alcopartyservice.repository.EventDaoFacade
import ru.buhinder.alcopartyservice.repository.EventPhotoDaoFacade
import java.util.UUID

@Component
class EventCreatorDelegate(
    private val conversionService: ConversionService,
    private val eventDaoFacade: EventDaoFacade,
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
    private val eventPhotoDaoFacade: EventPhotoDaoFacade,
) {

    fun create(dto: EventDto, eventCreator: UUID): Mono<IdResponse> {
        val eventModel = EventModel(dto, eventCreator)
        val entity = conversionService.convert(eventModel, EventEntity::class.java)!!
        val alcoholics = dto.alcoholicsIds
            .plus(eventCreator)
            .map { EventAlcoholicEntity(eventId = entity.id!!, alcoholicId = it) }
        val photos =
            dto.photosIds.map { EventPhotoEntity(eventId = entity.id!!, photoId = it, type = PhotoType.ACTIVE) }
        return entity.toMono()
            .flatMap { savedEntity ->
                eventDaoFacade.insert(savedEntity)
                    .flatMap { eventAlcoholicDaoFacade.insertAll(alcoholics) }
                    .flatMap { eventPhotoDaoFacade.insertAll(photos) }
                    .map { savedEntity }
            }
            .map { IdResponse(it.id!!) }
    }
}
