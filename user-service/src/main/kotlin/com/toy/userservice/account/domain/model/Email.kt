package com.toy.userservice.account.domain.model

import com.toy.userservice.account.domain.exception.InvalidEmailException

/**
 * 이메일
 */
@JvmInline
value class Email private constructor(val value: String) {

    companion object {
        private val EMAIL_REGEX = Regex(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )

        fun of(value: String): Email {
            val trimmed = value.trim().lowercase()

            require(trimmed.isNotBlank()) {
                throw InvalidEmailException("이메일은 비어있을 수 없습니다.")
            }

            require(EMAIL_REGEX.matches(trimmed)) {
                throw InvalidEmailException("유효하지 않은 이메일 형식입니다: $trimmed")
            }

            return Email(trimmed)
        }
    }

    override fun toString(): String = value
}
