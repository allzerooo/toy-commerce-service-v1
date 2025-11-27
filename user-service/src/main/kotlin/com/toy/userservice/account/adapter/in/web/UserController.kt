package com.toy.userservice.account.adapter.`in`.web

import com.toy.userservice.account.adapter.`in`.web.request.RegisterUserRequest
import com.toy.userservice.account.adapter.`in`.web.response.UserResponse
import com.toy.userservice.account.adapter.`in`.web.response.common.ApiResponse
import com.toy.userservice.account.application.port.`in`.RegisterUserUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 사용자 API
 */
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase
) {

    @PostMapping
    fun register(@Valid @RequestBody request: RegisterUserRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val user = registerUserUseCase.execute(request.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                data = UserResponse.from(user),
                message = "회원가입이 완료되었습니다."
            ))
    }
}
