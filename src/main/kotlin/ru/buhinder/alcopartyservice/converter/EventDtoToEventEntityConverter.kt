package ru.buhinder.alcopartyservice.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.entity.EventEntity
import ru.buhinder.alcopartyservice.entity.enums.EventStatus.ACTIVE
import ru.buhinder.alcopartyservice.entity.enums.EventStatus.SCHEDULED
import java.time.Instant
import java.util.UUID

@Component
class EventDtoToEventEntityConverter : Converter<EventDto, EventEntity> {

    override fun convert(source: EventDto): EventEntity {
        return EventEntity(
            UUID.randomUUID(),
            source.info,
            source.type,
            source.location,
            if (source.startDate > Instant.now().toEpochMilli()) SCHEDULED else ACTIVE,
            source.startDate,
            source.endDate
        )
    }

}
