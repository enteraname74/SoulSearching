package com.github.enteraname74.soulsearching.remote.model.user

import com.github.enteraname74.domain.model.ConnectedUser
import kotlinx.serialization.Serializable

@Serializable
data class RemoteConnectedUser(
    val username: String,
    val isAdmin: Boolean,
) {
    fun toConnectedUser(): ConnectedUser = ConnectedUser(
        username = username,
        isAdmin = isAdmin,
    )
}
