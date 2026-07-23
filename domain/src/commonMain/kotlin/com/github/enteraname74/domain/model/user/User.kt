package com.github.enteraname74.domain.model.user

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class User(
    val id: Uuid,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val type: UserType,
) {
    val isAdmin: Boolean = type == UserType.Admin
    fun hasCredentials(): Boolean =
        accessToken.isNotBlank() && refreshToken.isNotBlank()
}
