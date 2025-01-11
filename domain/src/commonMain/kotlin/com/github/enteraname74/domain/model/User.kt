package com.github.enteraname74.domain.model

data class User(
    val username: String,
    // Only used for view purposes
    val password: String,
) {
    fun isValid(): Boolean = username.isNotBlank()
}
