package com.toy.userservice.account.adapter.`in`.web.response

import com.toy.userservice.account.domain.model.User
import java.time.Instant

/**
 * 사용자 응답 API 계층 DTO
 */
data class UserResponse(
    val id: String,
    val email: String,
    val roles: List<String>,
    val status: String,
    val createdAt: Instant
) {
    companion object {
        /**
         * 도메인 모델 -> Response DTO 변환
         */
        fun from(user: User): UserResponse = UserResponse(
            id = user.id.toString(),
            email = user.email.value,
            roles = user.roles.map { it.name },
            status = user.status.name,
            createdAt = user.createdAt
        )
    }
}
