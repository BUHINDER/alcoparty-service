package ru.buhinder.alcopartyservice.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import java.util.UUID

open class AbstractAuditable(givenId: UUID? = null) {
    @Id
    open var id: UUID? = givenId ?: UUID.randomUUID()

    @Version
    open var version: Int? = null
}
