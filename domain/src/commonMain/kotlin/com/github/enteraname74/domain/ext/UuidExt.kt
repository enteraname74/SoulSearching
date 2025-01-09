package com.github.enteraname74.domain.ext

import java.util.*

fun String.toUUID(): UUID? = runCatching {
    UUID.fromString(this)
}.getOrNull()