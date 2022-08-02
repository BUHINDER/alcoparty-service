package ru.buhinder.alcopartyservice.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import ru.buhinder.alcopartyservice.entity.EventEntity
import ru.buhinder.alcopartyservice.entity.enums.EventStatus.ACTIVE
import ru.buhinder.alcopartyservice.entity.enums.EventStatus.SCHEDULED
import ru.buhinder.alcopartyservice.model.EventModel
import java.time.Instant
import java.util.UUID

@Component
class EventModelToEventEntityConverter : Converter<EventModel, EventEntity> {

    override fun convert(source: EventModel): EventEntity {
        val eventDto = source.eventDto
        val eventCreator = source.eventCreator
        return EventEntity(
            id = UUID.randomUUID(),
            info = eventDto.info,
            type = eventDto.type,
            location = eventDto.location,
            status = if (eventDto.startDate > Instant.now().toEpochMilli()) SCHEDULED else ACTIVE,
            startDate = eventDto.startDate,
            endDate = eventDto.endDate,
            eventCreator = eventCreator,
        )
    }

}
