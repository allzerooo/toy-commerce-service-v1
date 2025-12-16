package com.toy.userservice.account.adapter.`in`.web.common.security

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Spring Security 설정 클래스
 * 애플리케이션에 적용할 보안 필터 체인(SecurityFilterChain)을 구성
 * - CSRF, CORS, formLogin, httpBasic을 비활성화해 “브라우저 폼/세션 기반” 기본 인증 흐름을 끈다
 * - 세션을 만들지 않는 무상태(STATELESS) 정책을 사용해 요청마다 토큰으로 인증하는 API 방식
 * - JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 배치해, 요청의 Authorization(Bearer) 토큰을 먼저 검증하고 인증 정보를 SecurityContext에 세팅한다
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    /** local/dev 프로파일에서만 동작하도록 제한 */
    @Bean
    @Profile("local", "dev")
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .cors { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/v1/users").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/docs/**").permitAll()
                    .anyRequest().authenticated()
            }
            .headers { headers ->
                headers.frameOptions { it.disable() }
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}
