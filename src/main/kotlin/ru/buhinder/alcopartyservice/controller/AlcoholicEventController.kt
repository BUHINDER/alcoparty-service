package ru.buhinder.alcopartyservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.dto.response.EventResponse
import ru.buhinder.alcopartyservice.service.EventService
import java.security.Principal
import java.util.UUID

@RestController
@RequestMapping("/api/alcoparty/event/own")
class AlcoholicEventController(
    private val eventService: EventService,
) {

    @GetMapping
    fun getOwnEvents(principal: Principal): Mono<ResponseEntity<List<EventResponse>>> {
        return eventService.findAllByAlcoholicId(UUID.fromString(principal.name))
            .map { ResponseEntity.ok(it) }
    }

}
