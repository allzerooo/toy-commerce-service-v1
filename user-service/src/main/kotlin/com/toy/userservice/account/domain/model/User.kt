package com.toy.userservice.account.domain.model

import java.time.Instant

/**
 * 사용자 Aggregate Root
 */
class User private constructor(
    val id: UserId,
    val email: Email,
    private var _password: EncodedPassword,
    private var _roles: MutableSet<UserRole>,
    private var _status: UserStatus,
    val createdAt: Instant,
    private var _updatedAt: Instant
) {

    val password: EncodedPassword get() = _password
    val roles: Set<UserRole> get() = _roles.toSet()
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
                _roles = mutableSetOf(role),
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
            roles: Set<UserRole>,
            status: UserStatus,
            createdAt: Instant,
            updatedAt: Instant
        ): User = User(
            id = id,
            email = email,
            _password = password,
            _roles = roles.toMutableSet(),
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
     * 역할 추가 (예: 판매자 등록)
     */
    fun addRole(role: UserRole) {
        if (_roles.add(role)) {
            _updatedAt = Instant.now()
        }
    }

    /**
     * 역할 제거
     */
    fun removeRole(role: UserRole) {
        require(_roles.size > 1) { "최소 1개의 역할은 유지해야 합니다." }
        require(_roles.contains(role)) { "보유하지 않은 역할입니다: $role" }

        _roles.remove(role)
        _updatedAt = Instant.now()
    }

    /**
     * 특정 역할을 가지고 있는지 확인
     */
    fun hasRole(role: UserRole): Boolean = _roles.contains(role)

    /**
     * 구매자인지 확인
     */
    fun isBuyer(): Boolean = hasRole(UserRole.BUYER)

    /**
     * 판매자인지 확인
     */
    fun isSeller(): Boolean = hasRole(UserRole.SELLER)

    /**
     * 관리자인지 확인
     */
    fun isAdmin(): Boolean = hasRole(UserRole.ADMIN)

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

    override fun toString(): String = "User(id=$id, email=$email, roles=$roles, status=$status)"
}

/**
 * 사용자 상태
 */
enum class UserStatus {
    ACTIVE,      // 활성
    INACTIVE,    // 비활성 (탈퇴)
    SUSPENDED    // 정지
}
