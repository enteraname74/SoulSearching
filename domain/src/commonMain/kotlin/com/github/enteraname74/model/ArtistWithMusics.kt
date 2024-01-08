package com.github.enteraname74.model

/**
 * Represent an artist with its musics.
 */
data class ArtistWithMusics(
    val artist: Artist = Artist(),
    val musics : List<Music> = emptyList()
)