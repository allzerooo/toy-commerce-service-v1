package com.toy.userservice.account.adapter.out.persistence

import com.toy.userservice.account.adapter.out.persistence.jpa.UserJpaEntity.Companion.toJpaEntity
import com.toy.userservice.account.adapter.out.persistence.jpa.UserJpaRepository
import com.toy.userservice.account.application.exception.UserPersistenceException
import com.toy.userservice.account.application.port.out.UserCommandPort
import com.toy.userservice.account.application.port.out.UserQueryPort
import com.toy.userservice.account.domain.model.Email
import com.toy.userservice.account.domain.model.User
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
): UserCommandPort, UserQueryPort {

    override fun registerUser(user: User) {
        runCatching {
            userJpaRepository.save(user.toJpaEntity())
        }.onFailure { e ->
            throw UserPersistenceException(cause = e)
        }
    }

    override fun findByEmail(email: Email): User? {
        return userJpaRepository.findByEmail(email.value)?.toDomainEntity()
    }

    override fun existsByEmail(email: Email): Boolean {
        return userJpaRepository.existsByEmail(email.value)
    }
}
