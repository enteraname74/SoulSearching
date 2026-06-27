package com.github.enteraname74.domain.model.user

enum class UserType(val value: String) {
    User("user"),
    Admin("admin"),
    Unknown("unknown");

    companion object {
        fun fromValue(value: String): UserType? =
            entries.find { it.value == value }
    }
}