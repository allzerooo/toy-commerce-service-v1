package com.toy.userservice.account.application.service

import com.toy.userservice.account.application.port.`in`.RegisterUserUseCase
import com.toy.userservice.account.application.port.`in`.command.RegisterUserCommand
import com.toy.userservice.account.application.port.out.PasswordEncoder
import com.toy.userservice.account.application.port.out.UserCommandPort
import com.toy.userservice.account.domain.model.Email
import com.toy.userservice.account.domain.model.RawPassword
import com.toy.userservice.account.domain.model.User
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class RegisterUserService(
    private val passwordEncoder: PasswordEncoder,
    private val userCommandPort: UserCommandPort,
): RegisterUserUseCase {

    override fun execute(command: RegisterUserCommand): User {
        logger.info { "화원가입 요청: email=${command.email}" }

        val email = Email.of(command.email)
        val rawPassword = RawPassword.of(command.password)

        val encodedPassword = passwordEncoder.encode(rawPassword)

        val user = User.register(
            email = email,
            encodedPassword = encodedPassword,
            role = command.role,
        )

        userCommandPort.registerUser(user)

        logger.info { "화원가입 완료: email=${command.email}" }

        return user
    }
}
