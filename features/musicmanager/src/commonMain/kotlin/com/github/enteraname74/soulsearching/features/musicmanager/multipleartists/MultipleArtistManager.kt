package com.github.enteraname74.soulsearching.features.musicmanager.multipleartists

import com.github.enteraname74.domain.model.*
import java.util.UUID

/**
 * Manages multiple artist.
 * Multiple artist are Artist with multiple artists found in it (names separated by a comma).
 */
abstract class MultipleArtistManager {
    protected abstract suspend fun getAlbumsOfMultipleArtist(artist: Artist): List<Album>
    protected abstract suspend fun getArtistFromName(artistName: String): Artist?
    protected abstract suspend fun createNewArtist(artistName: String): Artist

    /**
     * Should only delete the Artist and not its songs, nor its albums
     * (each of the mentioned elements are managed by others functions)
     */
    protected abstract suspend fun deleteArtist(
        artist: Artist,
        musicIdsOfInitialArtist: List<UUID>,
        albumIdsOfInitialArtist: List<UUID>,
    )

    protected abstract suspend fun getMusicIdsOfArtist(artist: Artist): List<UUID>
    protected abstract suspend fun getAlbumIdsOfArtist(artist: Artist): List<UUID>

    protected abstract suspend fun linkMusicToArtist(
        musicId: UUID,
        artistId: UUID,
    )
    protected abstract suspend fun linkAlbumToArtist(
        albumId: UUID,
        artistId: UUID,
    )

    /**
     * Retrieve an album that has a specific name and is possessed by the first artist of the multiple artist.
     */
    protected abstract suspend fun getExistingAlbumOfFirstArtist(
        albumName: String,
        firstArtistName: String,
    ): Album?

    /**
     * Move the songs of an album to another.
     * fromAlbum should be deleted after the move.
     */
    protected abstract suspend fun moveSongsOfAlbum(
        fromAlbum: Album,
        toAlbum: Album,
        multipleArtistName: String,
    )

    /**
     * For a song with multiple artists, its albums should be linked to the first artist.
     * If there is already an existing album, with the same name and artist,
     * we delete the album of the multiple artists and link its songs to the found one.
     */
    private suspend fun manageMultipleArtistAlbums(
        multipleArtist: Artist,
        firstArtistName: String,
    ) {

        val albumsOfMultipleArtist: List<Album> = getAlbumsOfMultipleArtist(
            artist = multipleArtist,
        )

        albumsOfMultipleArtist.forEach { album ->
            /*
            If we found an existing album with a single artist as the first one of the multiple artist,
            we link the songs of the current album to it.
             */
            val albumWithSingleArtist: Album? = getExistingAlbumOfFirstArtist(
                albumName = album.albumName,
                firstArtistName = firstArtistName,
            )
            albumWithSingleArtist?.let {
                moveSongsOfAlbum(
                    fromAlbum = album,
                    toAlbum = it,
                    multipleArtistName = multipleArtist.artistName,
                )
            }
        }
    }

    /**
     * Divide a multiple artist into separated artists and link songs of the multiple artist to each of them.
     */
    private suspend fun divideArtistAndLinkSongsToThem(
        musicIdsOfInitialArtist: List<UUID>,
        albumIdsOfInitialArtist: List<UUID>,
        allArtistsName: List<String>
    ) {
        allArtistsName.forEachIndexed { index, name ->
            val artistId: UUID =
                getArtistFromName(name)?.artistId ?: createNewArtist(artistName = name).artistId

            musicIdsOfInitialArtist.forEach { musicId ->
                linkMusicToArtist(
                    musicId = musicId,
                    artistId = artistId,
                )
            }
            // Only the first artist of the list keep its album
            if (index == 0) {
                albumIdsOfInitialArtist.forEach { albumId ->
                    linkAlbumToArtist(
                        albumId = albumId,
                        artistId = artistId,
                    )
                }
            }
        }
    }

    suspend fun handleMultipleArtists(
        artistsToDivide: List<Artist>
    ) {
        // We update the concerned cached artists
        artistsToDivide.forEach { multipleArtist ->
            val musicIdsOfInitialArtist: List<UUID> = getMusicIdsOfArtist(
                artist = multipleArtist,
            )
            val albumIdsOfInitialArtist: List<UUID> = getAlbumIdsOfArtist(
                artist = multipleArtist,
            )

            val allArtistsName: List<String> = multipleArtist.getMultipleArtists()

            divideArtistAndLinkSongsToThem(
                musicIdsOfInitialArtist = musicIdsOfInitialArtist,
                albumIdsOfInitialArtist = albumIdsOfInitialArtist,
                allArtistsName = allArtistsName,
            )

            manageMultipleArtistAlbums(
                multipleArtist = multipleArtist,
                firstArtistName = allArtistsName.first(),
            )

            // We need to delete the links and artist with the legacy information:
            deleteArtist(
                artist = multipleArtist,
                musicIdsOfInitialArtist = musicIdsOfInitialArtist,
                albumIdsOfInitialArtist = albumIdsOfInitialArtist,
            )
        }
    }

    fun doMusicsHaveMultipleArtists(musics: List<Music>): Boolean =
        musics.any { it.hasPotentialMultipleArtists() }

    fun doArtistsHaveMultipleArtists(artists: List<Artist>): Boolean =
        artists.any { it.isComposedOfMultipleArtists() }
}