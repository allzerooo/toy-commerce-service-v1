package com.toy.userservice.account.adapter.`in`.web.common.security

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val kotlinLogger = KotlinLogging.logger {}

/**
 * JWT 기반 인증을 수행하는 필터
 *
 * 역할:
 * - 매 HTTP 요청마다 Authorization 헤더에서 Bearer 토큰을 꺼낸다
 * - 토큰이 있으면 유효성 검증(validateToken)을 수행한다
 * - 유효한 토큰이면 토큰에서 사용자 식별자(email)를 추출한다
 * - email로 UserDetails를 조회한 뒤 Authentication 객체를 만든다
 * - 생성한 Authentication을 SecurityContextHolder에 저장해, 이후 컨트롤러/서비스에서 “인증된 사용자”로 인식되게 한다
 *
 * 동작 특성:
 * - OncePerRequestFilter를 상속해 요청당 한 번만 실행된다
 * - 토큰이 없거나 검증에 실패하면 인증 정보를 세팅하지 않고 다음 필터로 넘긴다
 * - 예외가 발생해도 요청 처리를 중단하지 않고 로그만 남긴 뒤 다음 필터로 넘긴다
 */
@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = extractJwtFromRequest(request)

            if (jwt != null && jwtProvider.validateToken(jwt)) {
                val email = jwtProvider.getEmailFromToken(jwt)
                val userDetails = userDetailsService.loadUserByUsername(email)

                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authentication
                kotlinLogger.debug { "인증 성공: $email" }
            }
        } catch (e: Exception) {
            kotlinLogger.error(e) { "JWT 인증 처리 중 오류 발생" }
        }

        filterChain.doFilter(request, response)
    }

    private fun extractJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else {
            null
        }
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
}
