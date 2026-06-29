package com.github.enteraname74.soulsearching.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSignIn(
    val username: String,
    val password: String,
    val inscriptionCode: String,
)