package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.ext.coverFromSongs

/**
 * Represent an artist with its musics.
 */
data class ArtistWithMusics(
    val artist: Artist,
    val musics : List<Music>,
    override var isInQuickAccess: Boolean = artist.isInQuickAccess,
) : QuickAccessible {
    val cover: Cover? = if (artist.cover?.isEmpty() == false) {
        artist.cover
    } else {
        musics.coverFromSongs()
    }
}