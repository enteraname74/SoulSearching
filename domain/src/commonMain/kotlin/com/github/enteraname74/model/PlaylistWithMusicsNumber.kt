package com.github.enteraname74.model

/**
 * Represent a playlist with the total of songs it possess.
 */
data class PlaylistWithMusicsNumber(
    val playlist: Playlist = Playlist(),
    val musicsNumber : Int = 0
)
