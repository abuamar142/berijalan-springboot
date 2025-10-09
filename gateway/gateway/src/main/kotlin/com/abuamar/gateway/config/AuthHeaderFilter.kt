package com.abuamar.gateway.config

import com.abuamar.gateway.exception.CustomException
import com.abuamar.gateway.util.JTWUtil
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthHeaderFilter(
    private val jwtUtil: JTWUtil,
    private val authValidator: AuthValidator
): GatewayFilter {
    // Header authorization check
    fun isAuthExist(request: ServerHttpRequest): Boolean {
        return request.headers.containsKey("Authorization")
    }

    // Get token from header and validate it
    fun getToken(request: ServerHttpRequest): String? {
        val authHeader = request.headers.getOrEmpty("Authorization")

        if (authHeader.isEmpty()) return null

        val bearer = authHeader[0]

        if (bearer.split(" ").size == 2) {
            return bearer.split(" ")[1]
        } else {
            return null
        }
    }

    override fun filter(
        exchange: ServerWebExchange,
        chain: GatewayFilterChain
    ): Mono<Void> {
        val request = exchange.request

        // If url is not in white list, check auth
        if (authValidator.isSecure.test(request)) {
            if (!isAuthExist(request)) {
                throw CustomException(
                    "Authorization header is missing",
                    HttpStatus.UNAUTHORIZED.value()
                )
            }

            val token = getToken(request) ?: throw CustomException(
                "Authorization token is missing",
                HttpStatus.UNAUTHORIZED.value()
            )

            val claims = jwtUtil.decode(token)

            println("Claims: $claims")

            exchange.request.mutate()
                .header("X-USER-ID", claims["userId"].toString())
                .header("X-USER-AUTHORITY", claims["authority"].toString())
                .build()
        }

        // If url is in white list, skip auth check
        return chain.filter(exchange)
    }
}