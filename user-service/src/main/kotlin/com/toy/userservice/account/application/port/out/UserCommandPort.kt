package com.toy.userservice.account.application.port.out

import com.toy.userservice.account.domain.model.User

interface UserCommandPort {

    fun registerUser(user: User)
}
