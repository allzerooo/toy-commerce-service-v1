package com.toy.userservice.account.adapter.`in`.web.common.security

import com.toy.userservice.account.adapter.infrastructure.security.JwtProperties
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration

class JwtPropertiesTest {

    @Nested
    @DisplayName("생성 시")
    inner class Creation {

        @Test
        fun `유효한 설정으로 생성할 수 있다`() {
            val properties = JwtProperties(
                secret = "a".repeat(32),
                accessTokenExpiration = Duration.ofHours(1),
                refreshTokenExpiration = Duration.ofDays(14),
                issuer = "test-service"
            )

            assertThat(properties.secret).hasSize(32)
            assertThat(properties.accessTokenExpiration).isEqualTo(Duration.ofHours(1))
            assertThat(properties.refreshTokenExpiration).isEqualTo(Duration.ofDays(14))
            assertThat(properties.issuer).isEqualTo("test-service")
        }

        @Test
        fun `32바이트 미만의 secret은 거부한다`() {
            assertThatThrownBy {
                JwtProperties(
                    secret = "short-secret",
                    accessTokenExpiration = Duration.ofHours(1),
                    refreshTokenExpiration = Duration.ofDays(14),
                    issuer = "test-service"
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("32바이트 이상")
        }

        @Test
        fun `Access Token 만료 시간이 0 이하면 거부한다`() {
            assertThatThrownBy {
                JwtProperties(
                    secret = "a".repeat(32),
                    accessTokenExpiration = Duration.ZERO,
                    refreshTokenExpiration = Duration.ofDays(14),
                    issuer = "test-service"
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Access Token 만료 시간")
        }

        @Test
        fun `Refresh Token 만료 시간이 Access Token보다 짧으면 거부한다`() {
            assertThatThrownBy {
                JwtProperties(
                    secret = "a".repeat(32),
                    accessTokenExpiration = Duration.ofHours(2),
                    refreshTokenExpiration = Duration.ofHours(1),
                    issuer = "test-service"
                )
            }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("Refresh Token 만료 시간은 Access Token보다 길어야")
        }
    }

    @Nested
    @DisplayName("만료 시간 변환")
    inner class ExpirationConversion {

        private val properties = JwtProperties(
            secret = "a".repeat(32),
            accessTokenExpiration = Duration.ofHours(1),
            refreshTokenExpiration = Duration.ofDays(14),
            issuer = "test-service"
        )

        @Test
        fun `Access Token 만료 시간을 밀리초로 반환한다`() {
            assertThat(properties.accessTokenExpirationMillis())
                .isEqualTo(3600_000L) // 1시간 = 3,600,000ms
        }

        @Test
        fun `Refresh Token 만료 시간을 밀리초로 반환한다`() {
            assertThat(properties.refreshTokenExpirationMillis())
                .isEqualTo(14 * 24 * 3600_000L) // 14일
        }

        @Test
        fun `Access Token 만료 시간을 초로 반환한다`() {
            assertThat(properties.accessTokenExpirationSeconds())
                .isEqualTo(3600L) // 1시간 = 3,600초
        }
    }
}
