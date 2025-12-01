package com.toy.userservice.account.application.port.`in`

import com.toy.userservice.account.application.port.`in`.command.LoginCommand

/**
 * 로그인 Use Case
 */
interface LoginUseCase {
    /**
     * 로그인 실행
     * @return 액세스 토큰과 리프레시 토큰을 포함한 TokenPair
     */
    fun execute(command: LoginCommand): TokenPair
}

/**
 * 토큰 쌍 (액세스 토큰 + 리프레시 토큰)
 */
data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
