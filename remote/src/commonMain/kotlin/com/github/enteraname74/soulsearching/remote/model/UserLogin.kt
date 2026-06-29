package com.github.enteraname74.soulsearching.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserLogin(
    val username: String,
    val password: String,
)
