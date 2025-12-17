package com.toy.userservice.account.adapter.infrastructure.security

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * JWT 관련 설정을 담는 프로퍼티 클래스
 *
 * application.yml 예시:
 * ```yaml
 * jwt:
 *   secret: your-256-bit-secret-key-here-must-be-at-least-32-characters
 *   access-token-expiration: 1h
 *   refresh-token-expiration: 14d
 *   issuer: user-service
 * ```
 */
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessTokenExpiration: Duration,
    val refreshTokenExpiration: Duration,
    val issuer: String
) {
    init {
        require(secret.toByteArray().size >= 32) {
            "JWT secret은 최소 32바이트 이상이어야 합니다 (현재: ${secret.toByteArray().size}바이트)"
        }
        require(accessTokenExpiration > Duration.ZERO) {
            "Access Token 만료 시간은 0보다 커야 합니다"
        }
        require(refreshTokenExpiration > Duration.ZERO) {
            "Refresh Token 만료 시간은 0보다 커야 합니다"
        }
        require(refreshTokenExpiration > accessTokenExpiration) {
            "Refresh Token 만료 시간은 Access Token보다 길어야 합니다"
        }
    }

    /**
     * Access Token 만료 시간 (밀리초)
     */
    fun accessTokenExpirationMillis(): Long = accessTokenExpiration.toMillis()

    /**
     * Refresh Token 만료 시간 (밀리초)
     */
    fun refreshTokenExpirationMillis(): Long = refreshTokenExpiration.toMillis()

    /**
     * Access Token 만료 시간 (초) - API 응답용
     */
    fun accessTokenExpirationSeconds(): Long = accessTokenExpiration.toSeconds()
}
