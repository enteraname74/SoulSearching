package com.github.enteraname74.domain.model

import kotlin.uuid.Uuid

data class User(
    val id: Uuid,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val type: Type,
) {
    enum class Type(val value: String) {
        User("user"),
        Admin("admin"),
        Unknown("unknown");

        companion object {
            fun fromValue(value: String): Type? =
                entries.find { it.value == value }
        }
    }
}
