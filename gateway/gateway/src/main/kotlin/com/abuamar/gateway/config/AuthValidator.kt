package com.abuamar.gateway.config

import org.springframework.http.HttpRequest
import org.springframework.stereotype.Component
import java.util.function.Predicate

@Component
class AuthValidator {
    val whiteListEndpoints = listOf(
        "/auth/login",
        "/auth/register"
    )

    val isSecure = Predicate<HttpRequest> { request ->
        whiteListEndpoints.stream().noneMatch { path ->
            request.uri.path.contains(path)
        }
    }
}