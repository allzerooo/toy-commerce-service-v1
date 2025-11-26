package com.toy.userservice.account.application.exception

sealed class ApplicationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class PasswordProcessingException(
    message: String = "비밀번호 처리 중 오류가 발생했습니다.",
    cause: Throwable? = null
) : ApplicationException(message, cause)

class UserRoleRequiredException(
    message: String = "사용자는 최소 1개의 역할이 필요합니다.",
    cause: Throwable? = null
) : ApplicationException(message, cause)

class UserPersistenceException(
    message: String = "사용자 저장에 실패했습니다.",
    cause: Throwable? = null
) : ApplicationException(message, cause)
