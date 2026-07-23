package com.github.enteraname74.soulsearching.remote.model.upload

import com.github.enteraname74.domain.model.Music
import kotlinx.serialization.Serializable

@Serializable
data class MusicUpload(
    val name: String,
    val albumUpload: AlbumUpload,
    val artists: List<ArtistUpload>,
    val albumPosition: Int?,
    val duration: Long,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
)

fun Music.toMusicUpload(): MusicUpload =
    MusicUpload(
        name = name,
        albumUpload = album.toAlbumUpload(),
        artists = artists.map { it.toArtistUpload() },
        albumPosition = albumPosition,
        duration = duration,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
    )
