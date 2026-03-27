package com.github.enteraname74.soulsearching.remote.model.upload

import com.github.enteraname74.domain.model.Artist
import kotlinx.serialization.Serializable

@Serializable
data class ArtistUpload(
    val name: String,
    val nbPlayed: Int,
    val isInQuickAccess: Boolean,
)

fun Artist.toArtistUpload(): ArtistUpload =
    ArtistUpload(
        name = artistName,
        nbPlayed = nbPlayed,
        isInQuickAccess = isInQuickAccess,
    )