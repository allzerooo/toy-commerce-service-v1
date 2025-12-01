package com.toy.userservice.account.adapter.`in`.web.response

import com.toy.userservice.account.application.port.`in`.TokenPair

/**
 * 로그인 응답
 */
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
) {
    companion object {
        fun from(tokenPair: TokenPair): LoginResponse = LoginResponse(
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken
        )
    }
}
