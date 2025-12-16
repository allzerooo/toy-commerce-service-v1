package com.toy.userservice.account.adapter.out.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository: JpaRepository<UserJpaEntity, Long> {

    fun findByEmail(email: String): UserJpaEntity?

}
