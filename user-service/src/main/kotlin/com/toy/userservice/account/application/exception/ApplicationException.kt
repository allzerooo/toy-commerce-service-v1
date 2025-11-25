package com.toy.userservice.account.application.exception

sealed class ApplicationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class PasswordProcessingException(
    message: String = "비밀번호 처리 중 오류가 발생했습니다.",
    cause: Throwable? = null
) : ApplicationException(message, cause)
