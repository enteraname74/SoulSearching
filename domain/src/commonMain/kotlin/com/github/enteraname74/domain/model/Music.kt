package com.github.enteraname74.domain.model

import java.time.LocalDateTime
import java.util.*

/**
 * Represent a song with information related to it.
 * It does not possess its cover directly.
 */
data class Music(
    val musicId: UUID = UUID.randomUUID(),
    var name: String,
    val album: String,
    val albumArtist: String?,
    val artist: String,
    var cover: Cover,
    var duration: Long = 0L,
    val albumPosition: Int?,
    var path: String,
    var folder: String,
    var addedDate: LocalDateTime = LocalDateTime.now(),
    var nbPlayed: Int = 0,
    override var isInQuickAccess: Boolean = false,
    var isHidden: Boolean = false,
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