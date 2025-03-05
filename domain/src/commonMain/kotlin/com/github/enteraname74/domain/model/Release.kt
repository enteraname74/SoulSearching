package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable

/**
 * Information about a release of the application
 */
@Serializable
data class Release(
    val name: String,
    val tag: String,
    val githubUrl: String,
)
