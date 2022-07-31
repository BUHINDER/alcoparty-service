package ru.buhinder.alcopartyservice.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import ru.buhinder.alcopartyservice.entity.EventAlcoholicEntity
import java.util.UUID

interface EventAlcoholicReactiveCrudRepository : ReactiveCrudRepository<EventAlcoholicEntity, UUID>
