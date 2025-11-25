package com.toy.userservice.account.domain.exception

sealed class DomainException(
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * 유효하지 않은 이메일 예외
 */
class InvalidEmailException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)

/**
 * 유효하지 않은 비밀번호 예외
 */
class InvalidPasswordException(
    message: String,
    cause: Throwable? = null
) : DomainException(message, cause)
