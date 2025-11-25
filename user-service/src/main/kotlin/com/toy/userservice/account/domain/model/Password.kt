package com.toy.userservice.account.domain.model

import com.toy.userservice.account.domain.exception.InvalidPasswordException

/**
 * 비밀번호
 * - Raw Password: 암호화 전 원본 비밀번호 (검증용)
 * - Encoded Password: 암호화된 비밀번호 (저장용)
 */
sealed interface Password {
    val value: String
}

/**
 * 암호화 전 원본 비밀번호
 * - 생성 시점에 비밀번호 정책 검증
 */
@JvmInline
value class RawPassword private constructor(override val value: String) : Password {

    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 100

        // 최소 하나의 대문자, 소문자, 숫자, 특수문자 포함
        private val UPPERCASE_REGEX = Regex("[A-Z]")
        private val LOWERCASE_REGEX = Regex("[a-z]")
        private val DIGIT_REGEX = Regex("[0-9]")
        private val SPECIAL_CHAR_REGEX = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]")

        fun of(value: String): RawPassword {
            validate(value)
            return RawPassword(value)
        }

        private fun validate(password: String) {
            val errors = mutableListOf<String>()

            if (password.length < MIN_LENGTH) {
                errors.add("비밀번호는 최소 ${MIN_LENGTH}자 이상이어야 합니다.")
            }

            if (password.length > MAX_LENGTH) {
                errors.add("비밀번호는 최대 ${MAX_LENGTH}자 이하여야 합니다.")
            }

            if (!UPPERCASE_REGEX.containsMatchIn(password)) {
                errors.add("비밀번호에 대문자가 최소 1개 포함되어야 합니다.")
            }

            if (!LOWERCASE_REGEX.containsMatchIn(password)) {
                errors.add("비밀번호에 소문자가 최소 1개 포함되어야 합니다.")
            }

            if (!DIGIT_REGEX.containsMatchIn(password)) {
                errors.add("비밀번호에 숫자가 최소 1개 포함되어야 합니다.")
            }

            if (!SPECIAL_CHAR_REGEX.containsMatchIn(password)) {
                errors.add("비밀번호에 특수문자가 최소 1개 포함되어야 합니다.")
            }

            if (errors.isNotEmpty()) {
                throw InvalidPasswordException(errors.joinToString(" "))
            }
        }
    }
}

/**
 * 암호화된 비밀번호
 * - 저장 및 검증에 사용
 */
@JvmInline
value class EncodedPassword private constructor(override val value: String) : Password {

    companion object {
        fun of(value: String): EncodedPassword = EncodedPassword(value)
    }
}
