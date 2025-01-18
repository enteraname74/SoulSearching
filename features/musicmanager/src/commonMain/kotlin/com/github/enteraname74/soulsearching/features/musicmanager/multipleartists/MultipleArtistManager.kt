package com.github.enteraname74.soulsearching.features.musicmanager.multipleartists

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import java.util.*

/**
 * Manages multiple artist.
 * Multiple artist are Artist with multiple artists found in it (names separated by a comma).
 */
abstract class MultipleArtistManager {
    protected abstract suspend fun getAlbumsOfMultipleArtist(artist: Artist): List<Album>
    protected abstract suspend fun createNewArtist(artistName: String): Artist
    protected abstract suspend fun getArtistFromName(artistName: String): Artist?
    protected abstract suspend fun getAllArtistFromName(artistsNames: List<String>): List<Artist>

    /**
     * Should only delete artists and not its songs, nor its albums
     * (each of the mentioned elements are managed by others functions)
     */
    protected abstract suspend fun deleteArtists(artists: List<Artist>)

    protected abstract suspend fun getMusicIdsOfArtist(artist: Artist): List<UUID>
    protected abstract suspend fun getAlbumIdsOfArtist(artist: Artist): List<UUID>

    protected abstract suspend fun linkSongsToArtist(
        musicIds: List<UUID>,
        artistId: UUID,
    )

    protected abstract suspend fun linkAlbumToArtist(
        album: Album,
        artist: Artist,
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
        existingArtists: List<Artist>,
    ) {
        val albumsOfMultipleArtist: List<Album> = getAlbumsOfMultipleArtist(
            artist = multipleArtist
        )

        albumsOfMultipleArtist.forEach { album ->
            /*
            If we found an existing album with a single artist as the first one of the multiple artist,
            we link the songs of the current album to it. Else, we just link the album of the multiple artist to the first artist.
             */
            val albumWithSingleArtist: Album? = getExistingAlbumOfFirstArtist(
                albumName = album.albumName,
                firstArtistName = firstArtistName,
            )
            if (albumWithSingleArtist != null) {
                moveSongsOfAlbum(
                    fromAlbum = album,
                    toAlbum = albumWithSingleArtist,
                    multipleArtistName = multipleArtist.artistName,
                )
            } else {
                val artist: Artist = existingArtists.find { it.artistName == firstArtistName } ?: getArtistFromName(
                    firstArtistName
                ) ?: createNewArtist(firstArtistName)

                linkAlbumToArtist(
                    album = album,
                    artist = artist,
                )
            }
        }
    }

    /**
     * Divide a multiple artist into separated artists and link songs of the multiple artist to each of them.
     * All albums of the multiple artist will be linked to the first artist.
     */
    private suspend fun divideArtistAndLinkSongsToThem(
        musicIdsOfInitialArtist: List<UUID>,
        allArtistsName: List<String>,
        existingArtists: List<Artist>,
    ) {
        allArtistsName.forEach { name ->
            val artistId: UUID =
                (existingArtists.firstOrNull { it.artistName == name }
                    ?: getArtistFromName(name)
                    ?: createNewArtist(artistName = name)).artistId

            linkSongsToArtist(
                musicIds = musicIdsOfInitialArtist,
                artistId = artistId,
            )
        }
    }

    open suspend fun handleMultipleArtists(
        artistsToDivide: List<Artist>
    ) {
        val allExistingArtists: List<Artist> = getAllArtistFromName(
            artistsNames = buildList {
                artistsToDivide.forEach {
                    addAll(it.getMultipleArtists())
                }
            }.distinct()
        )

        // We update the concerned cached artists
        artistsToDivide.forEach { multipleArtist ->
            val musicIdsOfMultipleArtist: List<UUID> = getMusicIdsOfArtist(
                artist = multipleArtist,
            )

            val allArtistsName: List<String> = multipleArtist.getMultipleArtists()

            divideArtistAndLinkSongsToThem(
                musicIdsOfInitialArtist = musicIdsOfMultipleArtist,
                allArtistsName = allArtistsName,
                existingArtists = allExistingArtists,
            )

            manageMultipleArtistAlbums(
                multipleArtist = multipleArtist,
                firstArtistName = allArtistsName.first(),
                existingArtists = allExistingArtists,
            )
        }

        // We need to delete the links and artist with the legacy information:
        deleteArtists(artists = artistsToDivide)
    }

    fun doMusicsHaveMultipleArtists(musics: List<Music>): Boolean =
        musics.any { it.hasPotentialMultipleArtists() }

    fun doArtistsHaveMultipleArtists(artists: List<Artist>): Boolean =
        artists.any { it.isComposedOfMultipleArtists() }
}