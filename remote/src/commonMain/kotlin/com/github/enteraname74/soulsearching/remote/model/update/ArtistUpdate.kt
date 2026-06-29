package com.github.enteraname74.soulsearching.remote.model.update

import com.github.enteraname74.domain.model.Artist
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ArtistUpdate(
    val id: Uuid? = null,
    val name: String,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean
)

fun Artist.toArtistUpdate(): ArtistUpdate =
    ArtistUpdate(
        id = remoteId,
        name = artistName,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
    )
