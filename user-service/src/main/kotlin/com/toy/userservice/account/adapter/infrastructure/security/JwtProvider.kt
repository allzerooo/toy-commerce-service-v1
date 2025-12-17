package com.toy.userservice.account.adapter.infrastructure.security

import com.toy.userservice.account.domain.model.User
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

private val logger = KotlinLogging.logger {}

/**
 * JWT 토큰 생성 및 검증
 */
@Component
class JwtProvider(
    private val jwtProperties: JwtProperties
) {

    companion object {
        private const val JWT_CLAIM_EMAIL = "email"
        private const val JWT_CLAIM_ROLE = "role"
        private const val JWT_CLAIM_TOKEN_TYPE = "tokenType"
    }

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun createAccessToken(user: User): String {
        return createToken(user, jwtProperties.accessTokenExpiration, TokenType.ACCESS)
    }

    fun createRefreshToken(user: User): String {
        return createToken(user, jwtProperties.refreshTokenExpiration, TokenType.REFRESH)
    }

    private fun createToken(user: User, expiration: Duration, tokenType: TokenType): String {
        val now = Instant.now()
        val expiryInstant = now.plus(expiration)

        return Jwts.builder()
            .subject(user.id.toString())
            .issuer(jwtProperties.issuer)
            .claim(JWT_CLAIM_EMAIL, user.email.value)
            .claim(JWT_CLAIM_ROLE, user.role)
            .claim(JWT_CLAIM_TOKEN_TYPE, tokenType.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiryInstant))
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            !claims.expiration.before(Date())
        } catch (e: ExpiredJwtException) {
            logger.warn { "만료된 JWT 토큰: ${e.message}" }
            false
        } catch (e: JwtException) {
            logger.warn { "유효하지 않은 JWT 토큰: ${e.message}" }
            false
        } catch (e: IllegalArgumentException) {
            logger.warn { "JWT 토큰이 비어있음: ${e.message}" }
            false
        }
    }

    fun getEmailFromToken(token: String): String {
        return getClaims(token)[JWT_CLAIM_EMAIL] as String
    }

    fun getUserIdFromToken(token: String): String {
        return getClaims(token).subject
    }

    fun getRoleFromToken(token: String): String {
        return getClaims(token)[JWT_CLAIM_ROLE] as String
    }

    fun getAccessTokenExpiration(): Long {
        return jwtProperties.accessTokenExpirationSeconds()
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private enum class TokenType {
        ACCESS, REFRESH
    }
}
