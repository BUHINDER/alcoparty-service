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
import ru.buhinder.alcopartyservice.repository.facade.EventAlcoholicDaoFacade
import ru.buhinder.alcopartyservice.repository.facade.EventDaoFacade
import ru.buhinder.alcopartyservice.repository.facade.EventPhotoDaoFacade
import java.util.UUID

@Component
class EventCreatorDelegate(
    private val conversionService: ConversionService,
    private val eventDaoFacade: EventDaoFacade,
    private val eventAlcoholicDaoFacade: EventAlcoholicDaoFacade,
    private val eventPhotoDaoFacade: EventPhotoDaoFacade,
) {

    fun create(dto: EventDto, alcoholicId: UUID): Mono<IdResponse> {
        val eventModel = EventModel(dto, alcoholicId)
        val entity = conversionService.convert(eventModel, EventEntity::class.java)!!
        val alcoholics = dto.alcoholicsIds
            .plus(alcoholicId)
            .map { EventAlcoholicEntity(eventId = entity.id!!, alcoholicId = it) }
        val photos = dto.photosIds
            .map { EventPhotoEntity(eventId = entity.id!!, photoId = it, type = PhotoType.ACTIVE) }
        return entity.toMono()
            .flatMap { eventEntity ->
                eventDaoFacade.insert(eventEntity)
                    .flatMap { eventAlcoholicDaoFacade.insertAll(alcoholics) }
                    .flatMap { eventPhotoDaoFacade.insertAll(photos) }
                    .map { eventEntity }
            }
            .map { IdResponse(it.id!!) }
    }
}
