package ru.buhinder.alcopartyservice.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtConfig {

    @Bean
    fun jwtAlgorithm(): Algorithm {
        return Algorithm.HMAC256("BUHINDER")
    }

    @Bean
    fun jwtVerifier(): JWTVerifier {
        return JWT.require(jwtAlgorithm()).build()
    }

}
