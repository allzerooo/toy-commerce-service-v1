package com.toy.userservice.account.domain.model

import com.toy.userservice.account.domain.exception.InvalidEmailException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class EmailTest {

    @Test
    fun `유효한 이메일로 생성 성공`() {
        val email = Email.of("test@example.com")
        assertThat(email.value).isEqualTo("test@example.com")
    }

    @Test
    fun `이메일 소문자 변환`() {
        val email = Email.of("TEST@EXAMPLE.COM")
        assertThat(email.value).isEqualTo("test@example.com")
    }

    @Test
    fun `이메일 공백 제거`() {
        val email = Email.of("  test@example.com  ")
        assertThat(email.value).isEqualTo("test@example.com")
    }

    @Test
    fun `빈 이메일 예외 발생`() {
        assertThatThrownBy {
            Email.of("")
        }.isInstanceOf(InvalidEmailException::class.java)
    }

    @Test
    fun `공백만 있는 이메일 예외 발생`() {
        assertThatThrownBy {
            Email.of("   ")
        }.isInstanceOf(InvalidEmailException::class.java)
    }

    @Test
    fun `@가 없는 이메일 예외 발생`() {
        assertThatThrownBy {
            Email.of("testexample.com")
        }.isInstanceOf(InvalidEmailException::class.java)
    }

    @Test
    fun `도메인이 없는 이메일 예외 발생`() {
        assertThatThrownBy {
            Email.of("test@")
        }.isInstanceOf(InvalidEmailException::class.java)
    }
}
