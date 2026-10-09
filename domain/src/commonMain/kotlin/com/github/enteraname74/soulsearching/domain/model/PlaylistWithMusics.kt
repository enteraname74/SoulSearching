package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.soulsearching.domain.ext.coverFromSongs
import com.github.enteraname74.soulsearching.domain.ext.gridCoverIfPossible

/**
 * Represent a playlist with its songs.
 */
data class PlaylistWithMusics(
    val playlist: Playlist,
    val musics: List<Music>,
) {
    val cover: Cover? = when {
        playlist.cover?.isEmpty() != false -> musics.gridCoverIfPossible()
        playlist.cover is Cover.Simple -> playlist.cover.copyIfUrl { it.copy(fallback = musics.coverFromSongs()) }
        else -> playlist.cover
    }
}
