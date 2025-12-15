package com.toy.userservice.account.domain.model

import java.time.Instant

/**
 * 사용자 Aggregate Root
 */
class User private constructor(
    val id: UserId,
    val email: Email,
    val role: UserRole,
    private var _password: EncodedPassword,
    private var _status: UserStatus,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {

    val password: EncodedPassword get() = _password
    val status: UserStatus get() = _status
    val updatedAt: Instant get() = _updatedAt

    companion object {
        /**
         * 새로운 사용자 생성 (회원가입)
         */
        fun register(
            email: Email,
            encodedPassword: EncodedPassword,
            role: UserRole
        ): User {
            val now = Instant.now()
            return User(
                id = UserId.generate(),
                email = email,
                _password = encodedPassword,
                role = role,
                _status = UserStatus.ACTIVE,
                createdAt = now,
                _updatedAt = now
            )
        }

        /**
         * 영속성 계층에서 재구성할 때 사용
         */
        fun reconstitute(
            id: UserId,
            email: Email,
            password: EncodedPassword,
            role: UserRole,
            status: UserStatus,
            createdAt: Instant,
            updatedAt: Instant
        ): User = User(
            id = id,
            email = email,
            _password = password,
            role = role,
            _status = status,
            createdAt = createdAt,
            _updatedAt = updatedAt
        )
    }

    /**
     * 비밀번호 변경
     */
    fun changePassword(newEncodedPassword: EncodedPassword) {
        _password = newEncodedPassword
        _updatedAt = Instant.now()
    }

    /**
     * 구매자인지 확인
     */
    fun isBuyer(): Boolean = role == UserRole.BUYER

    /**
     * 판매자인지 확인
     */
    fun isSeller(): Boolean = role == UserRole.SELLER

    /**
     * 관리자인지 확인
     */
    fun isAdmin(): Boolean = role == UserRole.ADMIN

    /**
     * 계정 비활성화
     */
    fun deactivate() {
        _status = UserStatus.INACTIVE
        _updatedAt = Instant.now()
    }

    /**
     * 계정 활성화
     */
    fun activate() {
        _status = UserStatus.ACTIVE
        _updatedAt = Instant.now()
    }

    /**
     * 활성 상태인지 확인
     */
    fun isActive(): Boolean = _status == UserStatus.ACTIVE

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "User(id=$id, email=$email, role=$role, status=$status)"
}

/**
 * 사용자 상태
 */
enum class UserStatus {
    ACTIVE,      // 활성
    INACTIVE,    // 비활성 (탈퇴)
    SUSPENDED    // 정지
}
