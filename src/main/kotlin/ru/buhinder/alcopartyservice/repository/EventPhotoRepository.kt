package ru.buhinder.alcopartyservice.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import ru.buhinder.alcopartyservice.entity.EventPhotoEntity
import java.util.UUID

interface EventPhotoRepository : ReactiveCrudRepository<EventPhotoEntity, UUID>
