package com.toy.userservice.account.application.port.out

import com.toy.userservice.account.domain.model.EncodedPassword
import com.toy.userservice.account.domain.model.RawPassword

/**
 * 비밀번호 암호화
 */
interface PasswordEncoder {

    /**
     * 원본 비밀번호를 암호화
     */
    fun encode(rawPassword: RawPassword): EncodedPassword

    /**
     * 원본 비밀번호와 암호화된 비밀번호가 일치하는지 확인
     */
    fun matches(rawPassword: RawPassword, encodedPassword: EncodedPassword): Boolean
}
