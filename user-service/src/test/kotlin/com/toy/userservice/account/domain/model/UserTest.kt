package com.toy.userservice.account.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `회원가입으로 User 생성`() {
        val user = User.register(
            email = Email.of("test@example.com"),
            encodedPassword = EncodedPassword.of("encoded_password"),
            role = UserRole.BUYER,
        )

        assertThat(user.id).isNotNull()
        assertThat(user.email.value).isEqualTo("test@example.com")
        assertThat(user.status).isEqualTo(UserStatus.ACTIVE)
        assertThat(user.roles)
            .contains(UserRole.BUYER)
            .hasSize(1)
        assertThat(user.createdAt).isNotNull()
        assertThat(user.isActive()).isTrue()
    }

    @Test
    fun `판매자 역할 추가`() {
        val user = createTestUser()
        assertThat(user.isSeller()).isFalse()

        user.addRole(UserRole.SELLER)

        assertThat(user.isSeller()).isTrue()
        assertThat(user.roles)
            .contains(UserRole.BUYER, UserRole.SELLER)
            .hasSize(2)
        assertThat(user.hasRole(UserRole.BUYER)).isTrue()
        assertThat(user.hasRole(UserRole.SELLER)).isTrue()
    }

    @Test
    fun `관리자 역할 추가`() {
        val user = createTestUser()
        assertThat(user.isAdmin()).isFalse()

        user.addRole(UserRole.ADMIN)

        assertThat(user.isAdmin()).isTrue()
        assertThat(user.roles).hasSize(2)
    }

    @Test
    fun `판매자 역할 제거`() {
        val user = createTestUser()
        user.addRole(UserRole.SELLER)
        assertThat(user.isSeller()).isTrue()

        user.removeRole(UserRole.SELLER)

        assertThat(user.isSeller()).isFalse()
        assertThat(user.roles).hasSize(1)
    }

    @Test
    fun `역할은 최소 1개 이상`() {
        val user = createTestUser()

        assertThatThrownBy {
            user.removeRole(UserRole.BUYER)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `비밀번호 변경`() {
        val user = createTestUser()
        val originalUpdatedAt = user.updatedAt

        Thread.sleep(10)
        user.changePassword(EncodedPassword.of("new_encoded_password"))

        assertThat(user.password.value).isEqualTo("new_encoded_password")
        assertThat(user.updatedAt).isAfter(originalUpdatedAt)
    }

    @Test
    fun `계정 비활성화`() {
        val user = createTestUser()
        assertThat(user.isActive()).isTrue()

        user.deactivate()

        assertThat(user.status).isEqualTo(UserStatus.INACTIVE)
        assertThat(user.isActive()).isFalse()
    }

    @Test
    fun `계정 활성화`() {
        val user = createTestUser()
        user.deactivate()
        assertThat(user.isActive()).isFalse()

        user.activate()

        assertThat(user.status).isEqualTo(UserStatus.ACTIVE)
        assertThat(user.isActive()).isTrue()
    }

    @Test
    fun `동일 ID를 가진 User는 동일함`() {
        val user1 = createTestUser()
        val user2 = User.reconstitute(
            id = user1.id,
            email = Email.of("other@example.com"),
            password = EncodedPassword.of("other_password"),
            roles = setOf(UserRole.BUYER, UserRole.SELLER),
            status = UserStatus.ACTIVE,
            createdAt = user1.createdAt,
            updatedAt = user1.updatedAt
        )

        assertThat(user1).isEqualTo(user2)
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode())
    }

    private fun createTestUser(): User = User.register(
        email = Email.of("test@example.com"),
        encodedPassword = EncodedPassword.of("encoded_password"),
        role = UserRole.BUYER,
    )
}
