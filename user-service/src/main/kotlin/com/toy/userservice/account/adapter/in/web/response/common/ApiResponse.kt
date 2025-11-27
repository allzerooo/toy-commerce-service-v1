package com.toy.userservice.account.adapter.`in`.web.response.common

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val data: T? = null,
    val errors: List<FieldError>? = null,
    val timestamp: Instant = Instant.now()
) {
    companion object {

        fun <T> success(data: T, message: String = "요청이 성공했습니다."): ApiResponse<T> {
            return ApiResponse(
                success = true,
                code = "SUCCESS",
                message = message,
                data = data
            )
        }

        fun <T> success(message: String = "요청이 성공했습니다."): ApiResponse<T> {
            return ApiResponse(
                success = true,
                code = "SUCCESS",
                message = message,
                data = null
            )
        }

        fun <T> fail(
            code: String,
            message: String,
            errors: List<FieldError>? = null
        ): ApiResponse<T> {
            return ApiResponse(
                success = false,
                code = code,
                message = message,
                data = null,
                errors = errors
            )
        }

        fun <T> error(
            code: String = "INTERNAL_ERROR",
            message: String = "서버 내부 오류가 발생했습니다."
        ): ApiResponse<T> {
            return ApiResponse(
                success = false,
                code = code,
                message = message,
                data = null
            )
        }
    }
}

data class FieldError(
    val field: String,
    val message: String
)
