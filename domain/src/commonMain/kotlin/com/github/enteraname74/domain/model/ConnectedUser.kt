package com.github.enteraname74.domain.model

data class ConnectedUser(
    val username: String,
    val isAdmin: Boolean,
) {
    fun isValid(): Boolean = username.isNotBlank()
}
