package com.github.enteraname74.soulsearching.remote.model.upload

import com.github.enteraname74.domain.model.PlaylistWithMusics
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistUpload(
    val name: String,
    val isFavorite: Boolean,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val musicIds: List<String>,
)

fun PlaylistWithMusics.toPlaylistUpload(): PlaylistUpload =
    PlaylistUpload(
        name = playlist.name,
        isFavorite = playlist.isFavorite,
        nbPlayed = playlist.nbPlayed,
        isInQuickAccess = playlist.isInQuickAccess,
        musicIds = musics.mapNotNull { it.remoteId },
    )