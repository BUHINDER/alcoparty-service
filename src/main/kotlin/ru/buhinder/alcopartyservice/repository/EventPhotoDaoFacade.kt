package ru.buhinder.alcopartyservice.repository

import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.EventPhotoEntity

@Repository
class EventPhotoDaoFacade(
    private val r2dbcEntityOperations: R2dbcEntityOperations,
    private val eventPhotoReactiveCrudRepository: EventPhotoReactiveCrudRepository,
) {

    fun insert(eventPhotoEntity: EventPhotoEntity): Mono<EventPhotoEntity> {
        return r2dbcEntityOperations.insert(eventPhotoEntity)
    }

    fun insertAll(photos: List<EventPhotoEntity>): Mono<List<EventPhotoEntity>> {
        return eventPhotoReactiveCrudRepository.saveAll(photos)
            .collectList()
            .map { it.toList() }
    }

}
