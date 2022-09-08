package ru.buhinder.alcopartyservice.controller

import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.dto.EventDto
import ru.buhinder.alcopartyservice.dto.response.EventResponse
import ru.buhinder.alcopartyservice.dto.response.FullEventResponse
import ru.buhinder.alcopartyservice.dto.response.IdResponse
import ru.buhinder.alcopartyservice.service.EventAlcoholicService
import ru.buhinder.alcopartyservice.service.EventService
import java.security.Principal
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/api/alcoparty/event")
class EventController(
    private val eventService: EventService,
    private val eventAlcoholicService: EventAlcoholicService,
) {

    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    fun save(
        @Valid
        @RequestPart(value = "event")
        dto: EventDto,
        principal: Principal,
        @RequestPart(value = "images", required = false)
        files: Flux<FilePart>,
    ): Mono<ResponseEntity<FullEventResponse>> {
        return files
            .collectList()
            .flatMap { eventService.create(dto, UUID.fromString(principal.name), it.toList()) }
            .map { ResponseEntity.ok(it) }
    }

    @PutMapping("/join/{eventId}")
    fun join(@PathVariable eventId: UUID, principal: Principal): Mono<ResponseEntity<IdResponse>> {
        return eventService.join(eventId = eventId, alcoholicId = UUID.fromString(principal.name))
            .map { ResponseEntity.ok(it) }
    }

    @PutMapping("/leave/{eventId}")
    fun leave(@PathVariable eventId: UUID, principal: Principal): Mono<ResponseEntity<Void>> {
        return eventService.leave(eventId, UUID.fromString(principal.name))
            .map { ResponseEntity.ok().build() }
    }

    @PutMapping("/disband/{eventId}")
    fun disband(@PathVariable eventId: UUID, principal: Principal): Mono<ResponseEntity<Void>> {
        return eventService.disband(eventId, UUID.fromString(principal.name))
            .map { ResponseEntity.ok().build() }
    }

    @PutMapping("/block")
    fun block(
        @RequestParam("eventId") eventId: UUID,
        @RequestParam("userId") alcoholicId: UUID,
        principal: Principal,
    ): Mono<Boolean> {
        return eventAlcoholicService.block(eventId, alcoholicId, UUID.fromString(principal.name))
    }

    @GetMapping
    fun getAllEvents(principal: Principal): Mono<ResponseEntity<List<FullEventResponse>>> {
        return eventService.getAllEvents(UUID.fromString(principal.name))
            .collectList()
            .map { ResponseEntity.ok(it.toList()) }
    }

    @GetMapping("/{eventId}")
    fun getEventById(@PathVariable eventId: UUID, principal: Principal): Mono<ResponseEntity<FullEventResponse>> {
        return eventService.getEventById(eventId, UUID.fromString(principal.name))
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/own")
    fun getOwnEvents(principal: Principal): Mono<ResponseEntity<List<EventResponse>>> {
        return eventService.findAllByAlcoholicId(UUID.fromString(principal.name))
            .map { ResponseEntity.ok(it) }
    }

}
