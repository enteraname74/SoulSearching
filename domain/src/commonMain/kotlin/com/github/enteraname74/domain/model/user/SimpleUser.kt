package com.github.enteraname74.domain.model.user

import kotlin.uuid.Uuid

data class SimpleUser(
    val id: Uuid,
    val username: String,
    val type: UserType,
)
