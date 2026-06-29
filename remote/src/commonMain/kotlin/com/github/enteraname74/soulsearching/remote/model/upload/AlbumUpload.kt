package com.github.enteraname74.soulsearching.remote.model.upload

import com.github.enteraname74.domain.model.Album
import kotlinx.serialization.Serializable

@Serializable
data class AlbumUpload(
    val name: String,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val artistUpload: ArtistUpload,
)

fun Album.toAlbumUpload(): AlbumUpload =
    AlbumUpload(
        name = albumName,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        artistUpload = artist.toArtistUpload(),
    )