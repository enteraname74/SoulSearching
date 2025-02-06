package com.github.enteraname74.soulsearching.remote.model.user

import com.github.enteraname74.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserLogIn(
    val username: String,
    val password: String,
)

fun User.toUserLogIn(): UserLogIn =
    UserLogIn(
        username = username,
        password = password,
    )
