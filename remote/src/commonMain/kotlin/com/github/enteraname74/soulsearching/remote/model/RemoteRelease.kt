package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.Release
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteRelease(
    @SerialName("html_url")
    val githubUrl: String,
    @SerialName("tag_name")
    val tag: String,
    val name: String,
    val body: String?,
) {
    fun toRelease(): Release =
        Release(
            name = name,
            tag = tag,
            githubUrl = githubUrl,
            changelog = body?.takeIf { it.isNotBlank() },
        )
}