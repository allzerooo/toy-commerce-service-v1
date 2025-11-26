package com.toy.userservice.account.application.service

import com.toy.userservice.account.application.port.`in`.command.RegisterUserCommand
import com.toy.userservice.account.application.port.out.PasswordEncoder
import com.toy.userservice.account.application.port.out.UserCommandPort
import com.toy.userservice.account.domain.model.Email
import com.toy.userservice.account.domain.model.EncodedPassword
import com.toy.userservice.account.domain.model.RawPassword
import com.toy.userservice.account.domain.model.User
import com.toy.userservice.account.domain.model.UserRole
import com.toy.userservice.account.domain.model.UserStatus
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegisterUserServiceTest {

    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var userCommandPort: UserCommandPort
    private lateinit var registerUserService: RegisterUserService

    @BeforeEach
    fun setUp() {
        passwordEncoder = mockk()
        userCommandPort = mockk()
        registerUserService = RegisterUserService(passwordEncoder, userCommandPort)
    }

    @Test
    fun `회원가입 성공`() {
        // given
        val command = RegisterUserCommand(
            email = "test@example.com",
            password = "rawPassword123!",
            role = UserRole.BUYER
        )

        val encodedPassword = EncodedPassword.of("encodedPassword123")
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { userCommandPort.registerUser(any()) } just runs

        // when
        val result = registerUserService.execute(command)

        // then
        assertThat(result.email.value).isEqualTo("test@example.com")
        assertThat(result.password.value).isEqualTo("encodedPassword123")
        assertThat(result.roles).contains(UserRole.BUYER)
        assertThat(result.status).isEqualTo(UserStatus.ACTIVE)
        assertThat(result.id).isNotNull()
        assertThat(result.createdAt).isNotNull()
        assertThat(result.updatedAt).isNotNull()
    }

    @Test
    fun `PasswordEncoder가 올바르게 호출됨`() {
        // given
        val command = RegisterUserCommand(
            email = "test@example.com",
            password = "rawPassword123!",
            role = UserRole.BUYER
        )

        val rawPasswordSlot = slot<RawPassword>()
        val encodedPassword = EncodedPassword.of("encodedPassword123")
        every { passwordEncoder.encode(capture(rawPasswordSlot)) } returns encodedPassword
        every { userCommandPort.registerUser(any()) } just runs

        // when
        registerUserService.execute(command)

        // then
        verify(exactly = 1) { passwordEncoder.encode(any()) }
        assertThat(rawPasswordSlot.captured.value).isEqualTo("rawPassword123!")
    }

    @Test
    fun `UserCommandPort가 올바르게 호출됨`() {
        // given
        val command = RegisterUserCommand(
            email = "test@example.com",
            password = "rawPassword123!",
            role = UserRole.BUYER
        )

        val userSlot = slot<User>()
        val encodedPassword = EncodedPassword.of("encodedPassword123")
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { userCommandPort.registerUser(capture(userSlot)) } just runs

        // when
        registerUserService.execute(command)

        // then
        verify(exactly = 1) { userCommandPort.registerUser(any()) }
        assertThat(userSlot.captured.email.value).isEqualTo("test@example.com")
        assertThat(userSlot.captured.password.value).isEqualTo("encodedPassword123")
        assertThat(userSlot.captured.roles).contains(UserRole.BUYER)
        assertThat(userSlot.captured.status).isEqualTo(UserStatus.ACTIVE)
    }

    @Test
    fun `판매자 역할로 회원가입 성공`() {
        // given
        val command = RegisterUserCommand(
            email = "seller@example.com",
            password = "sellerPassword123!",
            role = UserRole.SELLER
        )

        val encodedPassword = EncodedPassword.of("encodedSellerPassword")
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { userCommandPort.registerUser(any()) } just runs

        // when
        val result = registerUserService.execute(command)

        // then
        assertThat(result.email.value).isEqualTo("seller@example.com")
        assertThat(result.password.value).isEqualTo("encodedSellerPassword")
        assertThat(result.roles).contains(UserRole.SELLER)
        assertThat(result.isSeller()).isTrue()
    }

    @Test
    fun `반환된 User 객체가 저장된 객체와 동일함`() {
        // given
        val command = RegisterUserCommand(
            email = "test@example.com",
            password = "rawPassword123!",
            role = UserRole.BUYER
        )

        val capturedUser = slot<User>()
        val encodedPassword = EncodedPassword.of("encodedPassword123")
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { userCommandPort.registerUser(capture(capturedUser)) } just runs

        // when
        val result = registerUserService.execute(command)

        // then
        assertThat(result).isEqualTo(capturedUser.captured)
        assertThat(result.id).isEqualTo(capturedUser.captured.id)
    }
}
