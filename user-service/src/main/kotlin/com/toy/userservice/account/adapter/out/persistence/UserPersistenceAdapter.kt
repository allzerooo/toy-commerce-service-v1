package com.toy.userservice.account.adapter.out.persistence

import com.toy.userservice.account.adapter.out.persistence.jpa.UserJpaEntity.Companion.toJpaEntity
import com.toy.userservice.account.adapter.out.persistence.jpa.UserJpaRepository
import com.toy.userservice.account.application.exception.UserPersistenceException
import com.toy.userservice.account.application.port.out.UserCommandPort
import com.toy.userservice.account.domain.model.User
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
): UserCommandPort {

    override fun registerUser(user: User) {
        runCatching {
            userJpaRepository.save(user.toJpaEntity())
        }.onFailure { e ->
            throw UserPersistenceException(cause = e)
        }
    }
}
