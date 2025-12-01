package com.toy.userservice.account.adapter.`in`.web.request

import com.toy.userservice.account.application.port.`in`.command.LoginCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * 로그인 요청
 */
data class LoginRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "유효한 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    val password: String
) {
    fun toCommand(): LoginCommand = LoginCommand(
        email = email,
        password = password
    )
}
