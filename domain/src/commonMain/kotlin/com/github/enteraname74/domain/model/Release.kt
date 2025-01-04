package com.github.enteraname74.domain.model

/**
 * Information about a release of the application
 */
data class Release(
    val name: String,
    val tag: String,
    val githubUrl: String,
)
