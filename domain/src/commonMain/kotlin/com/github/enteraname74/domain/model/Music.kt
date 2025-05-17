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
    val album: String,
    val albumArtist: String?,
    val artist: String,
    val cover: Cover,
    val duration: Long = 0L,
    val albumPosition: Int?,
    val path: String,
    val folder: String,
    val addedDate: LocalDateTime = LocalDateTime.now(),
    val nbPlayed: Int = 0,
    override val isInQuickAccess: Boolean = false,
    val isHidden: Boolean = false,
    val albumId: UUID,
) : QuickAccessible {
    val informationText: String = "$artist | $album"

    fun hasPotentialMultipleArtists(): Boolean = artist.split(",", "&").size > 1

    /**
     * The optimal artist of the album this music should belong to.
     */
    val artistOfAlbum: String
        get() = albumArtist ?: artist

    /**
     * Checks if the album artist is different from the artist of the music.
     */
    fun hasDifferentAlbumArtist(): Boolean =
        albumArtist != null && albumArtist.trim() != artist.trim()
}