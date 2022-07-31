package ru.buhinder.alcopartyservice.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.EventResponse
import ru.buhinder.alcopartyservice.service.EventService

@RestController
@RequestMapping("/api/alcoholic/event")
class EventController(
    private val eventService: EventService,
) {

    @PostMapping
    fun save(
        @Valid
        @RequestBody
        dto: EventDto,
    ): Mono<ResponseEntity<EventResponse>> {
        return eventService.create(dto)
            .map { ResponseEntity.ok(it) }
    }

}
