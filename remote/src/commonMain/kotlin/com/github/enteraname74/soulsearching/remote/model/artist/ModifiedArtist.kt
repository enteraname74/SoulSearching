package com.github.enteraname74.soulsearching.remote.model.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ModifiedArtist(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    var nbPlayed: Int = 0,
    var isInQuickAccess: Boolean = false,
)

fun Artist.toModifiedArtist() = ModifiedArtist(
    id = artistId,
    name = artistName,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
)