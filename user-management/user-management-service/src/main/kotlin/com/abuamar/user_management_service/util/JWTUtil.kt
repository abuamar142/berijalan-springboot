package com.abuamar.user_management_service.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JWTUtil(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,

    @Value("\${jwt.expired-in}")
    private val expiredIn: Int
) {
    fun generateToken(userId: Int,roleName: String?): String {
        val signatureAlgorithm = SignatureAlgorithm.HS256 // 32 bytes minimum
        val signingKey = Keys.hmacShaKeyFor(secretKey.toByteArray())
        val exp = Date(System.currentTimeMillis() + 864_000_00L * expiredIn) // 1 day

        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("userId", userId)
            .claim("authority", roleName ?: "user")
            .signWith(signingKey, signatureAlgorithm)
            .setExpiration(exp)
            .compact()
    }
}