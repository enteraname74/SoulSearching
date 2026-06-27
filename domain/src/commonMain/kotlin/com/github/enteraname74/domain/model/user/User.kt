package com.github.enteraname74.domain.model.user

import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val type: UserType,
) {
    val isAdmin = type == UserType.Admin
    fun hasCredentials(): Boolean =
        accessToken.isNotBlank() && refreshToken.isNotBlank()
}
