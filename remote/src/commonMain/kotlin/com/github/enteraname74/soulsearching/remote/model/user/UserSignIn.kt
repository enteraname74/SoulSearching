package com.github.enteraname74.soulsearching.remote.model.user

import com.github.enteraname74.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserSignIn(
    val username: String,
    val password: String,
    val inscriptionCode: String,
)

fun User.toUserSignIn(
    inscriptionCode: String,
): UserSignIn =
    UserSignIn(
        username = username,
        password = password,
        inscriptionCode = inscriptionCode,
    )