package ru.buhinder.alcopartyservice.service.strategy

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.buhinder.alcopartyservice.entity.enums.EventType

@Component
class EventStrategyRegistry(
    private val eventStrategies: Set<EventStrategy>,
) {
    private val map = createMap()

    fun get(eventType: EventType): Mono<EventStrategy> {
        return map[eventType]!!.toMono()
    }

    private fun createMap(): Map<EventType, EventStrategy> {
        return eventStrategies.associateBy { it.getEventType() }
    }

}
