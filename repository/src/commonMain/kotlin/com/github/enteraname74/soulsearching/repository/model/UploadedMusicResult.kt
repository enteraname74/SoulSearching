package com.github.enteraname74.soulsearching.repository.model

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music

sealed interface UploadedMusicResult {
    data class Data(
        val music: Music,
        val album: Album,
        val artists: List<Artist>,
    ): UploadedMusicResult

    data class AlreadySaved(
        val message: String,
    ): UploadedMusicResult
}
