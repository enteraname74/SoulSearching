package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.User
import kotlinx.serialization.Serializable
import java.rmi.Remote

@Serializable
data class RemoteUser(
    val username: String,
    val password: String,
)

fun User.toRemoteUser(): RemoteUser =
    RemoteUser(
        username = username,
        password = password,
    )
