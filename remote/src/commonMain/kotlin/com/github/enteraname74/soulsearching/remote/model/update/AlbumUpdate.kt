package com.github.enteraname74.soulsearching.remote.model.update

import com.github.enteraname74.domain.model.Album
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AlbumUpdate(
    val id: Uuid? = null,
    val name: String,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
    val artist: ArtistUpdate,
)

fun Album.toAlbumUpdate(): AlbumUpdate =
    AlbumUpdate(
        id = remoteId,
        name = albumName,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
        artist = artist.toArtistUpdate(),
    )