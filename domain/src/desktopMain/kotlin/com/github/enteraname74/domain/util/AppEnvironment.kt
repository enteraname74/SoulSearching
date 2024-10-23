package com.github.enteraname74.domain.util

object AppEnvironment {
    val IS_IN_DEVELOPMENT: Boolean = System.getenv("SOUL_DEV")?.toBoolean() ?: false
}