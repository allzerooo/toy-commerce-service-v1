package com.toy.userservice.account.adapter.out.persistence.jpa

import com.toy.userservice.account.application.exception.UserRoleRequiredException
import com.toy.userservice.account.domain.model.Email
import com.toy.userservice.account.domain.model.EncodedPassword
import com.toy.userservice.account.domain.model.User
import com.toy.userservice.account.domain.model.UserId
import com.toy.userservice.account.domain.model.UserRole
import com.toy.userservice.account.domain.model.UserStatus
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

/**
 * 사용자 JPA 엔티티
 */
@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_users_email", columnList = "email", unique = true)
    ]
)
class UserJpaEntity(
    uuid: UUID,
    email: String,
    password: String,
    role: UserRole,
    userStatus: UserStatus,
    createdAt: Instant? = null,
    updatedAt: Instant? = null,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(name = "uuid", nullable = false, unique = true, columnDefinition = "UUID")
    var uuid: UUID = uuid
        protected set

    @Column(name = "email", nullable = false, unique = true, length = 255)
    var email: String = email
        protected set

    @Column(name = "password", nullable = false, length = 255)
    var password: String = password
        protected set

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    var roles: MutableSet<UserRole> = mutableSetOf(role)
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: UserStatus = userStatus
        protected set

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = createdAt ?: Instant.now()
        protected set

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = updatedAt ?: Instant.now()
        protected set

    companion object {
        /**
         * 도메인 모델 -> JPA 엔티티 변환
         */
        fun User.toJpaEntity(): UserJpaEntity = UserJpaEntity(
            uuid = id.value,
            email = email.value,
            password = password.value,
            role = roles.firstOrNull() ?: throw UserRoleRequiredException(),
            userStatus = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    /**
     * JPA 엔티티 -> 도메인 모델 변환
     */
    fun toDomainEntity(): User = User.reconstitute(
        id = UserId.from(UUID.fromString(uuid.toString())),
        email = Email.of(email),
        password = EncodedPassword.of(password),
        roles = roles.toSet(),
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    /**
     * 도메인 모델의 변경사항을 엔티티에 반영
     */
    fun update(user: User) {
        this.password = user.password.value
        this.roles = user.roles.toMutableSet()
        this.status = user.status
        this.updatedAt = user.updatedAt
    }
}
