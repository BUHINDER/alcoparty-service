package ru.buhinder.alcopartyservice.config

import org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS
import org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS
import org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS
import org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class CorsFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        exchange.response.headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000")
        exchange.response.headers.add(ACCESS_CONTROL_ALLOW_METHODS, "GET, PUT, POST, DELETE, OPTIONS")
        exchange.response.headers.add(ACCESS_CONTROL_ALLOW_HEADERS, "Authorization, Content-Type")
        exchange.response.headers.add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
        return if (exchange.request.method == HttpMethod.OPTIONS) {
            exchange.response.statusCode = HttpStatus.NO_CONTENT
            Mono.empty()
        } else {
            chain.filter(exchange)
        }
    }

}
