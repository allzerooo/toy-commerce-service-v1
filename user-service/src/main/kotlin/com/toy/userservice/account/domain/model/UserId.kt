package com.toy.userservice.account.domain.model

import java.util.UUID

/**
 * 사용자 식별자
 */
@JvmInline
value class UserId private constructor(val value: UUID) {

    companion object {
        fun generate(): UserId = UserId(UUID.randomUUID())

        fun from(uuid: UUID): UserId = UserId(uuid)
    }

    override fun toString(): String = value.toString()
}
