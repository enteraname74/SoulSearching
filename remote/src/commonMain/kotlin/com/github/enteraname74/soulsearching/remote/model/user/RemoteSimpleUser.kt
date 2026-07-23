package com.github.enteraname74.soulsearching.remote.model.user

import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.UserType
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class RemoteSimpleUser(
    val id: Uuid,
    val username: String,
    val type: UserType,
) {
    fun toSimpleUsers(): SimpleUser =
        SimpleUser(
            id = id,
            username = username,
            type = type,
        )
}
