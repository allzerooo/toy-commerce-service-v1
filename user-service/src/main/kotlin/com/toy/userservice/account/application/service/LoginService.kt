package com.toy.userservice.account.application.service

import com.toy.userservice.account.adapter.out.security.JwtProvider
import com.toy.userservice.account.application.exception.InvalidCredentialsException
import com.toy.userservice.account.application.port.`in`.LoginUseCase
import com.toy.userservice.account.application.port.`in`.TokenPair
import com.toy.userservice.account.application.port.`in`.command.LoginCommand
import com.toy.userservice.account.application.port.out.PasswordEncoder
import com.toy.userservice.account.application.port.out.UserQueryPort
import com.toy.userservice.account.domain.model.Email
import com.toy.userservice.account.domain.model.RawPassword
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 로그인 서비스
 */
@Service
@Transactional(readOnly = true)
class LoginService(
    private val userQueryPort: UserQueryPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider
) : LoginUseCase {

    override fun execute(command: LoginCommand): TokenPair {
        // 1. 이메일로 사용자 조회
        val email = Email.of(command.email)
        val user = userQueryPort.findByEmail(email)
            ?: throw InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.")

        // 2. 비밀번호 검증
        val rawPassword = RawPassword.of(command.password)
        if (!passwordEncoder.matches(rawPassword, user.password)) {
            throw InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.")
        }

        // 3. 계정 상태 확인
        if (!user.isActive()) {
            throw InvalidCredentialsException("비활성화된 계정입니다.")
        }

        // 4. JWT 토큰 생성
        val accessToken = jwtProvider.generateAccessToken(
            userId = user.id.value.toString(),
            email = user.email.value,
            roles = user.roles.map { it.name }.toSet()
        )

        val refreshToken = jwtProvider.generateRefreshToken(
            userId = user.id.value.toString()
        )

        return TokenPair(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}
