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
        val eventModel = source.eventDto
        return EventEntity(
            id = UUID.randomUUID(),
            title = eventModel.title,
            info = eventModel.info,
            type = eventModel.type,
            location = eventModel.location,
            status = if (eventModel.startDate!! > Instant.now().toEpochMilli()) SCHEDULED else ACTIVE,
            startDate = eventModel.startDate,
            endDate = eventModel.endDate!!,
            createdBy = source.alcoholicId,
        )
    }

}
