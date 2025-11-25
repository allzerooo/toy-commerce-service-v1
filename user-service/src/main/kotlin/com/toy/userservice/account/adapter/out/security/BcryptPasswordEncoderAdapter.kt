package com.toy.userservice.account.adapter.out.security

import com.toy.userservice.account.application.exception.PasswordProcessingException
import com.toy.userservice.account.application.port.out.PasswordEncoder
import com.toy.userservice.account.domain.model.EncodedPassword
import com.toy.userservice.account.domain.model.RawPassword
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

/**
 * BCrypt 기반 비밀번호 암호화
 * - Spring Security의 BCryptPasswordEncoder 사용
 */
@Component
class BcryptPasswordEncoderAdapter : PasswordEncoder {

    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    override fun encode(rawPassword: RawPassword): EncodedPassword {
        val encoded = bCryptPasswordEncoder.encode(rawPassword.value)
            ?: throw PasswordProcessingException("비밀번호 암호화에 실패했습니다.")
        return EncodedPassword.of(encoded)
    }

    override fun matches(rawPassword: RawPassword, encodedPassword: EncodedPassword): Boolean {
        return bCryptPasswordEncoder.matches(rawPassword.value, encodedPassword.value)
    }
}
