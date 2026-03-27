package com.github.enteraname74.soulsearching.remote.model.update

import com.github.enteraname74.domain.model.Music
import kotlinx.serialization.Serializable

@Serializable
data class MusicUpdate(
    val id: String,
    val name: String,
    val album: AlbumUpdate,
    val artists: List<ArtistUpdate>,
    val albumPosition: Int?,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
)

fun Music.toMusicUpdate(): MusicUpdate? =
    remoteId?.let {
        MusicUpdate(
            id = it,
            name = name,
            album = album.toAlbumUpdate(),
            artists = artists.map { it.toArtistUpdate() },
            albumPosition = albumPosition,
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
        )
    }