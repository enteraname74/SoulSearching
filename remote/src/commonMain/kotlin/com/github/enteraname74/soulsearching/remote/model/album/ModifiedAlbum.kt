package com.github.enteraname74.soulsearching.remote.model.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ModifiedAlbum(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val artistName: String,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = true,
)

fun Album.toModifiedAlbum(artistName: String) = ModifiedAlbum(
    id = albumId,
    name = albumName,
    artistName = artistName,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
)