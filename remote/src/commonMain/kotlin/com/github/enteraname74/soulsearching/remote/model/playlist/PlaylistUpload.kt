package com.github.enteraname74.soulsearching.remote.model.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PlaylistUpsert(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val isFavorite: Boolean,
    val isInQuickAccess: Boolean,
)

fun Playlist.toPlaylistUpload() = PlaylistUpsert(
    id = playlistId,
    name = name,
    isFavorite = isFavorite,
    isInQuickAccess = isInQuickAccess
)
