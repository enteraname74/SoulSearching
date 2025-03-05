package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.Release
import kotlinx.serialization.Serializable

@Serializable
data class RemoteRelease(
    val html_url: String,
    val tag_name: String,
    val name: String,
) {
    fun toRelease(): Release =
        Release(
            name = name,
            tag = tag_name,
            githubUrl = html_url,
        )
}