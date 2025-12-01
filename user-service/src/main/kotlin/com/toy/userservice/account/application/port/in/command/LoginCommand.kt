package com.toy.userservice.account.application.port.`in`.command

/**
 * 로그인 명령
 */
data class LoginCommand(
    val email: String,
    val password: String
)
