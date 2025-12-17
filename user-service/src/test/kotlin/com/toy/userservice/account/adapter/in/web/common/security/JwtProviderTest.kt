package com.toy.userservice.account.adapter.`in`.web.common.security

import com.toy.userservice.account.adapter.infrastructure.security.JwtProperties
import com.toy.userservice.account.adapter.infrastructure.security.JwtProvider
import com.toy.userservice.account.domain.model.Email
import com.toy.userservice.account.domain.model.EncodedPassword
import com.toy.userservice.account.domain.model.User
import com.toy.userservice.account.domain.model.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import java.time.Duration
import kotlin.test.Test

class JwtProviderTest {

    private lateinit var jwtProvider: JwtProvider
    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        val jwtProperties = JwtProperties(
            secret = "test-secret-key-that-is-at-least-32-characters-long-for-testing",
            accessTokenExpiration = Duration.ofHours(1),
            refreshTokenExpiration = Duration.ofDays(14),
            issuer = "test-service"
        )
        jwtProvider = JwtProvider(jwtProperties)

        testUser = User.register(
            email = Email.of("test@example.com"),
            encodedPassword = EncodedPassword.of("encoded_password"),
            role = UserRole.BUYER,
        )
    }

    @Nested
    @DisplayName("토큰 생성")
    inner class TokenCreation {

        @Test
        fun `액세스 토큰을 생성할 수 있다`() {
            val token = jwtProvider.createAccessToken(testUser)

            assertThat(token).isNotBlank()
            assertThat(jwtProvider.validateToken(token)).isTrue()
        }

        @Test
        fun `리프레시 토큰을 생성할 수 있다`() {
            val token = jwtProvider.createRefreshToken(testUser)

            assertThat(token).isNotBlank()
            assertThat(jwtProvider.validateToken(token)).isTrue()
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    inner class TokenValidation {

        @Test
        fun `유효한 토큰은 검증을 통과한다`() {
            val token = jwtProvider.createAccessToken(testUser)

            assertThat(jwtProvider.validateToken(token)).isTrue()
        }

        @Test
        fun `잘못된 형식의 토큰은 검증에 실패한다`() {
            assertThat(jwtProvider.validateToken("invalid-token")).isFalse()
        }

        @Test
        fun `빈 토큰은 검증에 실패한다`() {
            assertThat(jwtProvider.validateToken("")).isFalse()
        }
    }

    @Nested
    @DisplayName("토큰에서 정보 추출")
    inner class TokenExtraction {

        @Test
        fun `토큰에서 이메일을 추출할 수 있다`() {
            val token = jwtProvider.createAccessToken(testUser)

            val email = jwtProvider.getEmailFromToken(token)

            assertThat(email).isEqualTo("test@example.com")
        }

        @Test
        fun `토큰에서 사용자 ID를 추출할 수 있다`() {
            val token = jwtProvider.createAccessToken(testUser)

            val userId = jwtProvider.getUserIdFromToken(token)

            assertThat(userId).isEqualTo(testUser.id.value.toString())
        }

        @Test
        fun `토큰에서 역할을 추출할 수 있다`() {
            val token = jwtProvider.createAccessToken(testUser)

            val role = jwtProvider.getRoleFromToken(token)

            assertThat(role).isEqualTo(UserRole.BUYER.name)
        }
    }

    @Nested
    @DisplayName("만료 시간")
    inner class Expiration {

        @Test
        fun `액세스 토큰 만료 시간을 초 단위로 반환한다`() {
            val expiration = jwtProvider.getAccessTokenExpiration()

            assertThat(expiration).isEqualTo(3600L) // 1시간 = 3600초
        }
    }
}
