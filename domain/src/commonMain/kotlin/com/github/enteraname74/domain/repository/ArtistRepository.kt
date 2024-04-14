package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.datasource.AlbumArtistDataSource
import com.github.enteraname74.domain.datasource.AlbumDataSource
import com.github.enteraname74.domain.datasource.ArtistDataSource
import com.github.enteraname74.domain.datasource.MusicAlbumDataSource
import com.github.enteraname74.domain.datasource.MusicArtistDataSource
import com.github.enteraname74.domain.datasource.MusicDataSource
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.util.MusicFileUpdater
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * DataSource of a Artist.
 */
class ArtistRepository(
    private val artistDataSource: ArtistDataSource,
    private val albumDataSource: AlbumDataSource,
    private val musicDataSource: MusicDataSource,
    private val musicArtistDataSource: MusicArtistDataSource,
    private val musicAlbumDataSource: MusicAlbumDataSource,
    private val albumArtistDataSource: AlbumArtistDataSource
) {

    private val musicFileUpdater: MusicFileUpdater = MusicFileUpdater()

    /**
     * Merge two artist together.
     * @param from the artist to put to the "to" artist.
     * @param to the artist that will receive the merge ("from" param)/
     */
    private suspend fun mergeArtists(from: ArtistWithMusics, to: Artist) {
        for (music in from.musics) {
            musicArtistDataSource.updateArtistOfMusic(
                musicId = music.musicId,
                newArtistId = to.artistId
            )
        }

        // We delete the previous artist.
        artistDataSource.deleteArtist(
            from.artist
        )
    }

    /**
     * Update the artist name of the artist songs.
     */
    private suspend fun updateArtistNameOfArtistSongs(
        newArtistName: String,
        artistMusics: List<Music>
    ) {
        for (music in artistMusics) {
            val newMusicInformation = music.copy(
                artist = newArtistName
            )
            musicDataSource.insertMusic(
                newMusicInformation
            )
            musicFileUpdater.updateMusic(
                music = newMusicInformation,
                cover = null
            )
        }
    }

    /**
     * Redirect the albums of an artist with the same name to the correct artist id.
     */
    private suspend fun redirectAlbumsToCorrectArtist(artist: Artist) {
        val legacyAlbumsOfArtist = albumDataSource.getAllAlbumsWithMusics().filter {
            it.artist!!.artistName == artist.artistName
        }

        /*
         If, once the artist name was changed,
         we have two albums with the same album and artist name,
         we redirect the album's musics that do not have the same artist's id as the actual one:
         */
        val albumsOrderedByAppearance =
            legacyAlbumsOfArtist.groupingBy { it.album.albumName }.eachCount()

        for (entry in albumsOrderedByAppearance.entries) {
            val albumWithMusicToUpdate = legacyAlbumsOfArtist.find {
                (it.album.albumName == entry.key)
                        && (it.artist!!.artistId != artist.artistId)
            }
            if (entry.value == 2) {
                // The album has a duplicate!
                // We redirect the album's songs to the one with the actual artist id.
                for (music in albumWithMusicToUpdate!!.musics) {
                    musicAlbumDataSource.updateAlbumOfMusic(
                        musicId = music.musicId,
                        newAlbumId = legacyAlbumsOfArtist.find {
                            (it.album.albumName == entry.key)
                                    && (it.artist!!.artistId == artist.artistId)
                        }!!.album.albumId
                    )
                }
                // We delete the previous album
                albumDataSource.deleteAlbum(
                    albumWithMusicToUpdate.album
                )
            } else if (albumWithMusicToUpdate != null) {
                // Sinon, on met à jour l'id de l'artiste
                albumArtistDataSource.updateArtistOfAlbum(
                    albumId = albumWithMusicToUpdate.album.albumId,
                    newArtistId = artist.artistId
                )
            }
        }
    }

    /**
     * Update an artist with new information.
     */
    suspend fun update(newArtistWithMusicsInformation: ArtistWithMusics) {
        artistDataSource.insertArtist(
            newArtistWithMusicsInformation.artist.copy(
                artistName = newArtistWithMusicsInformation.artist.artistName,
                coverId = newArtistWithMusicsInformation.artist.coverId
            )
        )

        // We redirect the possible artists albums that do not share the same artist id.
        redirectAlbumsToCorrectArtist(artist = newArtistWithMusicsInformation.artist)

        // We check if there is not two times the artist name.
        val possibleDuplicatedArtist: ArtistWithMusics? =
            artistDataSource.getPossibleDuplicatedArtist(
                artistName = newArtistWithMusicsInformation.artist.artistName,
                artistId = newArtistWithMusicsInformation.artist.artistId
            )

        // If so, we merge the duplicate one to the current artist.
        if (possibleDuplicatedArtist != null) mergeArtists(
            from = possibleDuplicatedArtist,
            to = newArtistWithMusicsInformation.artist
        )

        updateArtistNameOfArtistSongs(
            newArtistName = newArtistWithMusicsInformation.artist.artistName,
            artistMusics = newArtistWithMusicsInformation.musics
        )
    }

    /**
     * Inserts or updates an artist.
     */
    suspend fun insertArtist(artist: Artist) = artistDataSource.insertArtist(
        artist = artist
    )

    /**
     * Deletes an Artist.
     */
    suspend fun deleteArtist(artist: Artist) = artistDataSource.deleteArtist(
        artist = artist
    )

    /**
     * Retrieves an Artist from its id.
     */
    suspend fun getArtistFromId(artistId: UUID): Artist? = artistDataSource.getArtistFromId(
        artistId = artistId
    )

    /**
     * Retrieves a flow of all Artist, sorted by name asc.
     */
    fun getAllArtistsSortByNameAscAsFlow(): Flow<List<Artist>> =
        artistDataSource.getAllArtistsSortByNameAscAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name asc.
     */
    fun getAllArtistsWithMusicsSortByNameAscAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistsWithMusicsSortByNameAscAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name desc.
     */
    fun getAllArtistWithMusicsSortByNameDescAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsSortByNameDescAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by added date asc.
     */
    fun getAllArtistWithMusicsSortByAddedDateAscAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsSortByAddedDateAscAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by added date desc.
     */
    fun getAllArtistWithMusicsSortByAddedDateDescAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsSortByAddedDateDescAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by nb played asc.
     */
    fun getAllArtistWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsSortByNbPlayedAscAsFlow()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by nb played desc.
     */
    fun getAllArtistWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsSortByNbPlayedDescAsFlow()

    /**
     * Tries to find a duplicate artist.
     */
    suspend fun getPossibleDuplicatedArtist(artistId: UUID, artistName: String): ArtistWithMusics? =
        artistDataSource.getPossibleDuplicatedArtist(
            artistId = artistId,
            artistName = artistName
        )

    /**
     * Tries to find an artist from its name.
     */
    suspend fun getArtistFromInfo(artistName: String): Artist? = artistDataSource.getArtistFromInfo(
        artistName = artistName
    )

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    fun getArtistWithMusicsAsFlow(artistId: UUID): Flow<ArtistWithMusics?> =
        artistDataSource.getArtistWithMusicsAsFlow(
            artistId = artistId
        )

    /**
     * Retrieves all ArtistWithMusics from the quick access
     */
    fun getAllArtistWithMusicsFromQuickAccessAsFlow(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusicsFromQuickAccessAsFlow()

    /**
     * Retrieves an ArtistWithMusics.
     */
    suspend fun getArtistWithMusics(artistId: UUID): ArtistWithMusics? =
        artistDataSource.getArtistWithMusics(
            artistId = artistId
        )

    /**
     * Update the cover of an Artist.
     */
    suspend fun updateArtistCover(newCoverId: UUID, artistId: UUID) =
        artistDataSource.updateArtistCover(
            newCoverId = newCoverId,
            artistId = artistId
        )

    /**
     * Get the number of artists sharing the same cover.
     */
    suspend fun getNumberOfArtistsWithCoverId(coverId: UUID): Int =
        artistDataSource.getNumberOfArtistsWithCoverId(
            coverId = coverId
        )

    /**
     * Update the quick access status of an Artist.
     */
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, artistId: UUID) =
        artistDataSource.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            artistId = artistId
        )

    /**
     * Get the number of time an Artist has been played.
     */
    suspend fun getNbPlayedOfArtist(artistId: UUID): Int = artistDataSource.getNbPlayedOfArtist(
        artistId = artistId
    )

    /**
     * Update the total of played time of an Artist.
     */
    suspend fun updateNbPlayed(newNbPlayed: Int, artistId: UUID) = artistDataSource.updateNbPlayed(
        newNbPlayed = newNbPlayed,
        artistId = artistId
    )
}