package ru.buhinder.alcopartyservice.security.converter

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.entity.enums.Role
import ru.buhinder.alcopartyservice.service.TokenService


@Component
class JwtServerAuthenticationConverter(
    private val tokenService: TokenService,
) : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return Mono.justOrEmpty(exchange)
            .flatMap { webEx ->
                Mono.justOrEmpty(webEx.request.headers["Authorization"])
                    .map { it.first() }
                    .flatMap { tokenService.validateToken(it) }
                    .filter { it.claims.contains("roles") }
                    .onErrorResume { Mono.empty() }
            }
            .map {
                UsernamePasswordAuthenticationToken(
                    it.subject,
                    null,
                    tokenService.buildAuthorities(it.claims["roles"]!!.asArray(Role::class.java))
                )
            }
    }

}
