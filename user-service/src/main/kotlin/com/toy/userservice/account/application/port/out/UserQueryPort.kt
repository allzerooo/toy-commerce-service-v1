package com.toy.userservice.account.application.port.out

import com.toy.userservice.account.domain.model.Email
import com.toy.userservice.account.domain.model.User

/**
 * 사용자 조회 포트
 */
interface UserQueryPort {
    /**
     * 이메일로 사용자 조회
     */
    fun findByEmail(email: Email): User?

    /**
     * 이메일 존재 여부 확인
     */
    fun existsByEmail(email: Email): Boolean
}
