package com.github.enteraname74.domain.model

import com.github.enteraname74.domain.ext.coverFromSongs

/**
 * Represent a playlist with its songs.
 */
data class PlaylistWithMusics(
    val playlist: Playlist,
    val musics : List<Music>,
) {

    val cover: Cover? = if (playlist.cover?.isEmpty() == false) {
        playlist.cover
    } else {
        musics.coverFromSongs()
    }

    /**
     * Convert a PlaylistWithMusics to a PlaylistWithMusicsNumber.
     */
    fun toPlaylistPreview(): PlaylistPreview {
        return PlaylistPreview(
            id = playlist.playlistId,
            isFavorite = playlist.isFavorite,
            name = playlist.name,
            totalMusics = musics.filter { !it.isHidden }.size,
            cover = cover,
            isInQuickAccess = playlist.isInQuickAccess,
        )
    }
}