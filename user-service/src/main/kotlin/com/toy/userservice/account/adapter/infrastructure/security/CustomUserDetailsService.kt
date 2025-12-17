package com.toy.userservice.account.adapter.infrastructure.security

import com.toy.userservice.account.adapter.out.persistence.jpa.UserJpaRepository
import com.toy.userservice.account.domain.model.Email
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Spring Security가 “인증 대상 사용자 정보를 조회”할 때 사용하는 서비스이다
 *
 * 역할
 * - 전달받은 username(여기서는 이메일 문자열)을 이메일 값 객체(Email)로 변환해 형식을 검증한다
 * - 이메일로 DB에서 사용자를 조회한다
 * - 사용자가 없거나 이메일 형식이 잘못되면 UsernameNotFoundException을 던져 Spring Security가 “인증 실패(사용자 없음/잘못된 식별자)”로 처리하게 한다
 * - 조회된 사용자 도메인 객체를 CustomUserDetails로 감싸서 반환한다
 *   (권한(authorities), 계정 상태 등의 정보를 Spring Security가 이해할 수 있는 형태로 제공)
 *
 * 사용처
 * - JwtAuthenticationFilter 같은 인증 필터에서 토큰의 email을 기반으로 사용자를 로드할 때 호출된다
 */
@Service
class CustomUserDetailsService(
    private val userRepository: UserJpaRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val email = try {
            Email.Companion.of(username)
        } catch (e: IllegalArgumentException) {
            throw UsernameNotFoundException("유효하지 않은 이메일 형식입니다: $username")
        }

        val user = userRepository.findByEmail(email.value)?.toDomainEntity()
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다: $username")

        return CustomUserDetails(user)
    }
}
