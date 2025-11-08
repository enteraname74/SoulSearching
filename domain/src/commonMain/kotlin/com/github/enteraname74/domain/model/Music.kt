package com.github.enteraname74.domain.model

import java.time.LocalDateTime
import java.util.*

/**
 * Represent a song with information related to it.
 * It does not possess its cover directly.
 */
data class Music(
    val musicId: UUID = UUID.randomUUID(),
    val name: String,
    val album: Album,
    val withAlbumArtist: Boolean,
    val artists: List<Artist>,
    val cover: Cover,
    val albumPosition: Int?,
    val path: String,
    val folder: String,
    val duration: Long = 0L,
    val addedDate: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    override val isInQuickAccess: Boolean = false,
    val isHidden: Boolean = false,
) : QuickAccessible {
    val informationText: String = "${artists.joinToString { it.artistName }} | ${album.albumName}"

    val artistsNames: String = artists.joinToString { it.artistName }

    fun hasPotentialMultipleArtists(): Boolean = artists
        .any {
            it
                .artistName
                .split(",", "&")
                .size > 1
        }

    override fun toString(): String =
        "Music(name: $name, album: $album, artists: $artists)"
}