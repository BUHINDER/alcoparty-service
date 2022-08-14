package ru.buhinder.alcopartyservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.EventResponse
import ru.buhinder.alcopartyservice.dto.response.IdResponse
import ru.buhinder.alcopartyservice.service.EventService
import java.security.Principal
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/alcoparty/event")
class EventController(
    private val eventService: EventService,
) {

    @PostMapping
    fun save(
        @Valid
        @RequestBody
        dto: EventDto,
        principal: Principal,
    ): Mono<ResponseEntity<IdResponse>> {
        return eventService.create(dto, UUID.fromString(principal.name))
            .map { ResponseEntity.ok(it) }
    }

    @PutMapping("/{eventId}")
    fun join(@PathVariable eventId: UUID, principal: Principal): Mono<ResponseEntity<IdResponse>> {
        return eventService.join(eventId = eventId, alcoholicId = UUID.fromString(principal.name))
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping
    fun getAllEvents(principal: Principal): Mono<ResponseEntity<List<EventResponse>>> {
        return eventService.getAllEvents(UUID.fromString(principal.name))
            .collectList()
            .map { ResponseEntity.ok(it.toList()) }
    }

    @GetMapping("/{eventId}")
    fun getEventById(@PathVariable eventId: UUID, principal: Principal): Mono<ResponseEntity<EventResponse>> {
        return eventService.getEventById(eventId, UUID.fromString(principal.name))
            .map { ResponseEntity.ok(it) }
    }

}
