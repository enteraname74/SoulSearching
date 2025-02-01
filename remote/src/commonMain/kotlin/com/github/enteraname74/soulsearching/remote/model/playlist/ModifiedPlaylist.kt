package com.github.enteraname74.soulsearching.remote.model.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ModifiedPlaylist(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
)

fun Playlist.toModifiedPlaylist(): ModifiedPlaylist =
    ModifiedPlaylist(
        id = playlistId,
        name = name,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
    )
