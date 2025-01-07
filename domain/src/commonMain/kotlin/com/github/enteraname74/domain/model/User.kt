package com.github.enteraname74.domain.model

data class User(
    val username: String,
    val password: String,
) {
    fun isValid(): Boolean = username.isNotBlank() && password.isNotBlank()
}
