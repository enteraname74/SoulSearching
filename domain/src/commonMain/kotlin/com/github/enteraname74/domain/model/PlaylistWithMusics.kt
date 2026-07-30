package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.ext.coverFromSongs

/**
 * Represent a playlist with its songs.
 */
data class PlaylistWithMusics(
    val playlist: Playlist,
    val musics: List<Music>,
) {
    val cover: Cover? = if (playlist.cover?.isEmpty() == false) {
        playlist.cover.copyIfUrl { it.copy(fallback = musics.coverFromSongs()) }
    } else {
        musics.coverFromSongs()
    }
}
