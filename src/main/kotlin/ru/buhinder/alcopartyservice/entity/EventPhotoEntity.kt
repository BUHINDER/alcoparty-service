package ru.buhinder.alcopartyservice.entity

import org.springframework.data.relational.core.mapping.Table
import ru.buhinder.alcopartyservice.entity.enums.PhotoType
import java.util.UUID

@Table("event_photo")
open class EventPhotoEntity(
    id: UUID? = null,
    val eventId: UUID,
    val photoId: UUID,
    val type: PhotoType,
) : AbstractAuditable(id)
