package com.toy.userservice.account.application.port.`in`

import com.toy.userservice.account.application.port.`in`.command.RegisterUserCommand
import com.toy.userservice.account.domain.model.User

interface RegisterUserUseCase {

    fun execute(command: RegisterUserCommand): User
}
