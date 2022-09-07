package ru.buhinder.alcopartyservice.entity

import org.springframework.data.annotation.Id
import java.util.UUID

open class AbstractAuditable(givenId: UUID? = null) {
    @Id
    open var id: UUID? = givenId ?: UUID.randomUUID()
}
