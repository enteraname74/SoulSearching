package com.github.enteraname74.soulsearching.domain.model

import kotlinx.serialization.Serializable

/**
 * Information about a release of the application
 */
@Serializable
data class Release(
    val name: String,
    val tag: String,
    val githubUrl: String,
    val changelog: String?,
    val apkAsset: ReleaseAsset? = null,
)

@Serializable
data class ReleaseAsset(
    val name: String,
    val downloadUrl: String,
    val size: Long,
    val sha256: String?,
)
