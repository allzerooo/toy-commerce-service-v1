package com.toy.userservice.account.adapter.out.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

/**
 * JWT 토큰 생성 및 검증
 */
@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.access-token-expiration}")
    private val accessTokenExpiration: Long,

    @Value("\${jwt.refresh-token-expiration}")
    private val refreshTokenExpiration: Long
) {

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * Access Token 생성
     */
    fun generateAccessToken(userId: String, email: String, roles: Set<String>): String {
        val now = Instant.now()
        val expiration = Date.from(now.plusMillis(accessTokenExpiration))

        return Jwts.builder()
            .subject(userId)
            .claim("email", email)
            .claim("roles", roles)
            .claim("type", "access")
            .issuedAt(Date.from(now))
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    /**
     * Refresh Token 생성
     */
    fun generateRefreshToken(userId: String): String {
        val now = Instant.now()
        val expiration = Date.from(now.plusMillis(refreshTokenExpiration))

        return Jwts.builder()
            .subject(userId)
            .claim("type", "refresh")
            .issuedAt(Date.from(now))
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    /**
     * 토큰 검증 및 Claims 추출
     */
    fun validateAndGetClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    fun getUserId(token: String): String {
        return validateAndGetClaims(token).subject
    }

    /**
     * 토큰 유효성 검증
     */
    fun validateToken(token: String): Boolean {
        return try {
            validateAndGetClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}
