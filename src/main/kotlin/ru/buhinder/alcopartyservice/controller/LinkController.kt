package ru.buhinder.alcopartyservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.dto.response.IdResponse
import ru.buhinder.alcopartyservice.service.InvitationLinkService
import java.security.Principal
import java.util.UUID

@RestController
@RequestMapping("/api/alcoparty/link")
class LinkController(
    private val invitationLinkService: InvitationLinkService,
) {

    @PostMapping("/{eventId}")
    fun create(
        @PathVariable
        eventId: UUID,
        principal: Principal,
    ): Mono<ResponseEntity<IdResponse>> {
        return invitationLinkService.create(eventId, UUID.fromString(principal.name))
            .map { ResponseEntity.ok(it) }
    }

    @PutMapping("/{invitationLink}")
    fun join(@PathVariable invitationLink: UUID, principal: Principal): Mono<ResponseEntity<IdResponse>> {
        return invitationLinkService.join(invitationLink, UUID.fromString(principal.name))
            .map { ResponseEntity.ok(it) }
    }

}
