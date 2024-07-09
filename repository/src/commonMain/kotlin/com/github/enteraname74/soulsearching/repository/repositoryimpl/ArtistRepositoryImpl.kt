package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.util.MusicFileUpdater
import com.github.enteraname74.soulsearching.repository.datasource.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*

/**
 * DataSource of a Artist.
 */
class ArtistRepositoryImpl(
    private val artistDataSource: ArtistDataSource,
    private val albumDataSource: AlbumDataSource,
    private val musicDataSource: MusicDataSource,
    private val musicArtistDataSource: MusicArtistDataSource,
    private val musicAlbumDataSource: MusicAlbumDataSource,
    private val albumArtistDataSource: AlbumArtistDataSource
): ArtistRepository {

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
        artistDataSource.delete(
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
        val legacyAlbumsOfArtist = albumDataSource.getAllAlbumsWithMusics().first().filter {
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
                albumDataSource.delete(
                    albumWithMusicToUpdate.album
                )
            } else if (albumWithMusicToUpdate != null) {
                // Sinon, on met à jour l'id de l'artiste
                albumArtistDataSource.update(
                    albumId = albumWithMusicToUpdate.album.albumId,
                    newArtistId = artist.artistId
                )
            }
        }
    }

    /**
     * Update an artist with new information.
     */
    override suspend fun update(newArtistWithMusicsInformation: ArtistWithMusics) {
        artistDataSource.upsert(
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
    override suspend fun upsert(artist: Artist) = artistDataSource.upsert(
        artist = artist
    )

    /**
     * Deletes an Artist.
     */
    override suspend fun delete(artist: Artist) = artistDataSource.delete(
        artist = artist
    )

    /**
     * Retrieves an Artist from its id.
     */
    override fun getFromId(artistId: UUID): Flow<Artist?> = artistDataSource.getFromId(
        artistId = artistId
    )

    /**
     * Retrieves a flow of all Artist, sorted by name asc.
     */
    override fun getAll(): Flow<List<Artist>> =
        artistDataSource.getAll()

    /**
     * Retrieves a flow of all ArtistWithMusics, sorted by name asc.
     */
    override fun getAllArtistWithMusics(): Flow<List<ArtistWithMusics>> =
        artistDataSource.getAllArtistWithMusics()

    /**
     * Retrieves a flow of an ArtistWithMusics.
     */
    override fun getArtistWithMusics(artistId: UUID): Flow<ArtistWithMusics?> =
        artistDataSource.getArtistWithMusics(
            artistId = artistId
        )
}