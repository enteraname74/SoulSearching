package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.User
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class RemoteUserAuth(
    val user: ConnectedUser,
    val tokens: UserTokens,
) {
    fun toUser(): User =
        User(
            id = user.id,
            username = user.username,
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            type = if (user.isAdmin) User.Type.Admin else User.Type.User,
        )
}

@Serializable
data class ConnectedUser(
    val id: Uuid,
    val username: String,
    // TODO CLOUD: Update backend to support user type enum.
    val isAdmin: Boolean,
)

@Serializable
data class UserTokens(
    val accessToken: String,
    val refreshToken: String,
)