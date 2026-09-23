package com.github.enteraname74.soulsearching.domain.util

object AppEnvironment {
    val IS_IN_DEVELOPMENT: Boolean = System.getenv("SOUL_DEV")?.toBoolean() == true
}
