package com.toy.userservice.account.domain.model

import com.toy.userservice.account.domain.exception.InvalidPasswordException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class PasswordTest {

    @Test
    fun `유효한 비밀번호로 생성 성공`() {
        assertThatCode {
            RawPassword.of("Test1234!")
        }.doesNotThrowAnyException()
    }

    @Test
    fun `8자 미만 비밀번호 예외 발생`() {
        assertThatThrownBy {
            RawPassword.of("Test1!")  // 6자
        }.isInstanceOf(InvalidPasswordException::class.java)
    }

    @Test
    fun `대문자 없는 비밀번호 예외 발생`() {
        assertThatThrownBy {
            RawPassword.of("test1234!")
        }.isInstanceOf(InvalidPasswordException::class.java)
    }

    @Test
    fun `소문자 없는 비밀번호 예외 발생`() {
        assertThatThrownBy {
            RawPassword.of("TEST1234!")
        }.isInstanceOf(InvalidPasswordException::class.java)
    }

    @Test
    fun `숫자 없는 비밀번호 예외 발생`() {
        assertThatThrownBy {
            RawPassword.of("TestTest!")
        }.isInstanceOf(InvalidPasswordException::class.java)
    }

    @Test
    fun `특수문자 없는 비밀번호 예외 발생`() {
        assertThatThrownBy {
            RawPassword.of("Test12345")
        }.isInstanceOf(InvalidPasswordException::class.java)
    }

    @Test
    fun `EncodedPassword 생성 성공`() {
        val encoded = EncodedPassword.of("\$2a\$10\$encodedpassword")

        assertThat(encoded.value).isEqualTo("\$2a\$10\$encodedpassword")
    }
}
