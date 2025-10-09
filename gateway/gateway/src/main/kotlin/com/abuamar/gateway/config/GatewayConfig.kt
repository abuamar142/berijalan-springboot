package com.abuamar.gateway.config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class GatewayConfig(
    private val authHeaderFilter: AuthHeaderFilter
) {
    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator? {
        return builder.routes()
            .route("user-management-service") { r: PredicateSpec ->
                r.path("/gateway/user-management/**")
                    .filters { f: GatewayFilterSpec ->
                        f.rewritePath(
                            "/gateway/user-management/(?<segment>.*)",
                            "/user-management/\${segment}"
                        )
                        // Apply the auth filter
                        f.filters(authHeaderFilter)
                    }
                    .uri("lb://user-management-service")
            }.route("hotel-management-service") { r: PredicateSpec ->
                r.path("/gateway/hotel-management/**")
                    .filters { f: GatewayFilterSpec ->
                        f.rewritePath(
                            "/gateway/hotel-management/(?<segment>.*)",
                            "/hotel-management/\${segment}"
                        )
                        // Apply the auth filter
                        f.filters(authHeaderFilter)
                    }
                    .uri("lb://hotel-management-service")
            }.build()
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .csrf { it.disable() }

        return http.build()
    }
}