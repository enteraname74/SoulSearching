package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.soulsearching.domain.ext.coverFromSongs

/**
 * Represent an album with its songs and artist.
 */
data class AlbumWithMusics(
    val album: Album,
    val musics: List<Music>,
) {

    val cover: Cover.Simple? = if (album.cover?.isEmpty() == false) {
        album.cover.copyIfUrl { it.copy(fallback = musics.coverFromSongs()) }
    } else {
        musics.coverFromSongs()
    }
}
