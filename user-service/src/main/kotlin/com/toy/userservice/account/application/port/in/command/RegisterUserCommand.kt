package com.toy.userservice.account.application.port.`in`.command

import com.toy.userservice.account.domain.model.UserRole

/**
 * 회원가입 명령 객체
 */
data class RegisterUserCommand(
    val email: String,
    val password: String,
    val role: UserRole,
)
