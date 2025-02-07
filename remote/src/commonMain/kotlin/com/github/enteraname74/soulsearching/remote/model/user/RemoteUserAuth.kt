package com.github.enteraname74.soulsearching.remote.model.user

import com.github.enteraname74.soulsearching.repository.model.UserAuth
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUserAuth(
    val user: RemoteConnectedUser,
    val tokens: RemoteUserTokens,
) {
    fun toUserAuth(): UserAuth = UserAuth(
        user = user.toConnectedUser(),
        tokens = tokens.toUserTokens(),
    )
}
