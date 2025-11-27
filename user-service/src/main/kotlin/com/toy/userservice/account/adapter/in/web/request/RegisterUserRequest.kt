package com.toy.userservice.account.adapter.`in`.web.request

import com.toy.userservice.account.application.port.`in`.command.RegisterUserCommand
import com.toy.userservice.account.domain.model.UserRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * 회원가입 요청 API 계층 DTO
 */
data class RegisterUserRequest(
    @field:NotBlank(message = "이메일은 필수 값입니다.")
    @field:Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "유효하지 않은 이메일 형식입니다"
    )
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수 값입니다.")
    @field:Size(
        min = 8,
        max = 100,
        message = "비밀번호는 8자 이상 100자 이하여야 합니다"
    )
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#\\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,100}$",
        message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    val password: String,

    val userRole: UserRole
) {
    /**
     * Application Command로 변환
     */
    fun toCommand(): RegisterUserCommand = RegisterUserCommand(
        email = email,
        password = password,
        role = userRole,
    )
}
