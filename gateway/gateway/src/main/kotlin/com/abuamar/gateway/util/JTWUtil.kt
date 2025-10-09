package com.abuamar.gateway.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JTWUtil(
    @param:Value("\${jwt.secret-key}")
    private val secretKey: String,
) {
    fun decode(jwt: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secretKey.toByteArray())) // Get from application.yaml
            .build()
            .parseClaimsJws(jwt) // Compare build result with jwt
            .body
    }
}