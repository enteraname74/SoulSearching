package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.Release
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteRelease(
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("tag_name")
    val tagName: String,
    val name: String,
) {
    fun toRelease(): Release =
        Release(
            name = name,
            tag = tagName,
            githubUrl = htmlUrl,
        )
}