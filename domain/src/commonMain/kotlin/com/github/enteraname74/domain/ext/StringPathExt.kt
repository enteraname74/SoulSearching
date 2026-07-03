package com.github.enteraname74.domain.ext

fun String.filenameFromPath(): String {
    val normalized = trimEnd('/', '\\')
    if (normalized.isBlank()) return normalized

    return normalized
        .substringAfterLast('/')
        .substringAfterLast('\\')
}
