package com.github.enteraname74.soulsearching.remote.model.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ModifiedMusic(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val album: String,
    val nbPlayed: Int = 0,
    val isInQuickAccess: Boolean = false,
    val artists: List<String>,
)

fun Music.toModifiedMusic(artists: List<String>) = ModifiedMusic(
    id = musicId,
    name = name,
    album = album,
    nbPlayed = nbPlayed,
    isInQuickAccess = isInQuickAccess,
    artists = artists,
)