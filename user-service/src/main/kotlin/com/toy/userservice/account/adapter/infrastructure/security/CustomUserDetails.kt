package com.toy.userservice.account.adapter.infrastructure.security

import com.toy.userservice.account.domain.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * 도메인 사용자(User)를 Spring Security가 이해할 수 있는 “인증 사용자 정보(UserDetails)”로 감싸는 어댑터이다
 *
 * 역할
 * - User의 이메일을 username으로 제공한다(getUsername)
 * - User의 비밀번호 해시 값을 password로 제공한다(getPassword)
 * - User의 역할(role)을 Spring Security 권한(GrantedAuthority) 형태로 변환한다
 *   예: ROLE_ADMIN, ROLE_USER 같은 문자열로 반환(getAuthorities)
 * - 계정 만료/잠금/비활성화 여부를 Spring Security에 알려준다
 *   (현재 구현은 모두 true로 고정되어 항상 정상 계정으로 취급한다)
 *
 * 사용처
 * - CustomUserDetailsService가 DB에서 조회한 User를 감싼 뒤 반환한다
 * - JwtAuthenticationFilter가 인증 성공 시 Authentication의 principal로 넣어
 *   이후 컨트롤러/서비스에서 인증된 사용자 정보를 꺼낼 수 있게 한다
 */
class CustomUserDetails(
    private val user: User
) : UserDetails {

    fun getUser(): User = user

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
    }

    override fun getPassword(): String = user.password.value

    override fun getUsername(): String = user.email.value

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
