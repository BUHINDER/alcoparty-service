package ru.buhinder.alcopartyservice.service

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.buhinder.alcopartyservice.config.LoggerDelegate
import ru.buhinder.alcopartyservice.entity.enums.Role

@Service
class TokenService(
    private val jwtVerifier: JWTVerifier,
) {
    private val logger by LoggerDelegate()

    fun buildAuthorities(roles: Array<Role>): List<SimpleGrantedAuthority> {
        return roles
            .map { SimpleGrantedAuthority(it.name) }
            .toList()
    }

    fun validateToken(token: String): Mono<DecodedJWT> {
        return Mono.just(logger.info("Trying to validate token"))
            .map { token.replace("Bearer ", "") }
            .map { jwtVerifier.verify(it) }
            .doOnSuccess { logger.info("Validated token successfully") }
            .doOnError { logger.info("Error validated token") }
    }

}
