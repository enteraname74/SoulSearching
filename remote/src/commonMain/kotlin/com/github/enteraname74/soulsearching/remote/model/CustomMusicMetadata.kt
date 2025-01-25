package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.Music
import kotlinx.serialization.Serializable

@Serializable
data class CustomMusicMetadata(
    val name: String?,
    val album: String?,
    val artists: List<String>,
    val duration: Long,
) {
    companion object {
        fun fromMusic(
            music: Music,
            artists: List<String>,
        ): CustomMusicMetadata =
            CustomMusicMetadata(
                name = music.name,
                album = music.album,
                artists = artists,
                duration = music.duration,
            )
    }
}