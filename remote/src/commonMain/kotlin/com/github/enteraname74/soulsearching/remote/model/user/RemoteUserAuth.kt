package com.github.enteraname74.soulsearching.remote.model.user

import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.model.user.UserTokens
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUserAuth(
    val user: RemoteSimpleUser,
    val tokens: UserTokens,
) {
    fun toUser(): User =
        User(
            id = user.id,
            username = user.username,
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            type = user.type,
        )
}