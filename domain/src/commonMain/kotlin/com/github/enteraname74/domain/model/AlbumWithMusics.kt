package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.ext.coverFromSongs

/**
 * Represent an album with its songs and artist.
 */
data class AlbumWithMusics(
    val album: Album,
    val musics : List<Music>,
    override val isInQuickAccess: Boolean = album.isInQuickAccess,
): QuickAccessible {

    val cover: Cover? = if (album.cover?.isEmpty() == false) {
        album.cover
    } else {
        musics.coverFromSongs()
    }
}