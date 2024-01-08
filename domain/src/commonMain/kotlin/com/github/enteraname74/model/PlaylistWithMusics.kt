package com.github.enteraname74.model

/**
 * Represent a playlist with its songs.
 */
data class PlaylistWithMusics(
    val playlist: Playlist = Playlist(),
    val musics : List<Music> = emptyList()
) {

    /**
     * Convert a PlaylistWithMusics to a PlaylistWithMusicsNumber.
     */
    fun toPlaylistWithMusicsNumber(): PlaylistWithMusicsNumber {
        return PlaylistWithMusicsNumber(
            playlist = playlist,
            musicsNumber = musics.filter { !it.isHidden }.size
        )
    }
}