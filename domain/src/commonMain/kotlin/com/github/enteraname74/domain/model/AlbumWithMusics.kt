package com.github.enteraname74.domain.model

/**
 * Represent an album with its songs and artist.
 */
data class AlbumWithMusics(
    val album: Album = Album(),
    val musics : List<Music> = emptyList(),
    val artist: Artist? = Artist(),
) {
    /**
     * Convert an AlbumWithMusics to an AlbumWithArtist only.
     */
    fun toAlbumWithArtist() : AlbumWithArtist = AlbumWithArtist(
        album = album,
        artist = artist
    )
}