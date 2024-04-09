package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.datasource.AlbumArtistDataSource
import com.github.enteraname74.domain.datasource.AlbumDataSource
import com.github.enteraname74.domain.datasource.ArtistDataSource
import com.github.enteraname74.domain.datasource.MusicAlbumDataSource
import com.github.enteraname74.domain.datasource.MusicArtistDataSource
import com.github.enteraname74.domain.datasource.MusicDataSource
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.util.CheckAndDeleteVerification
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository of an Album.
 */
class AlbumRepository(
    private val albumDataSource: AlbumDataSource,
    private val musicDataSource: MusicDataSource,
    private val artistDataSource: ArtistDataSource,
    private val albumArtistDataSource: AlbumArtistDataSource,
    private val musicAlbumDataSource: MusicAlbumDataSource,
    private val musicArtistDataSource: MusicArtistDataSource,
    private val checkAndDeleteVerification: CheckAndDeleteVerification
) {

    /**
     * Merge two albums together.
     * @param from the album to put to the "to" album.
     * @param to the album that will receive the merge ("from" param)/
     */
    private suspend fun mergeAlbums(from: Album, to: Album) {
        // We update the link of the musics of the duplicated album to the new album id.
        musicAlbumDataSource.updateMusicsAlbum(
            newAlbumId = to.albumId,
            legacyAlbumId = from.albumId
        )
        // We remove the previous album.
        albumArtistDataSource.deleteAlbumFromArtist(
            albumId = from.albumId
        )
        albumDataSource.deleteAlbum(
            album = from
        )
    }

    /**
     * Update an album with new infomation.
     */
    suspend fun update(
        newAlbumWithArtistInformation: AlbumWithArtist
    ) {
        val initialArtist = artistDataSource.getArtistFromId(
            artistId = newAlbumWithArtistInformation.artist!!.artistId
        ) ?: return


        var albumArtistToSave = initialArtist
        if (newAlbumWithArtistInformation.artist.artistName != initialArtist.artistName) {
            // We first try to find if there is an existing artist with the new artist name.
            var newArtist = artistDataSource.getArtistFromInfo(
                artistName = newAlbumWithArtistInformation.artist.artistName
            )
            // If this artist doesn't exist, we create it.
            if (newArtist == null) {
                newArtist = Artist(
                    artistName = newAlbumWithArtistInformation.artist.artistName,
                    coverId = newAlbumWithArtistInformation.album.coverId
                )
                artistDataSource.insertArtist(
                    artist = newArtist
                )
            }
            // We update the link between the album and its artist.
            albumArtistDataSource.updateArtistOfAlbum(
                albumId = newAlbumWithArtistInformation.album.albumId,
                newArtistId = newArtist.artistId
            )
            albumArtistToSave = newArtist
        }

        // We then check if there is an album with the same name and artist that already exist.
        val duplicateAlbum = albumDataSource.getPossibleDuplicateAlbum(
            albumId = newAlbumWithArtistInformation.album.albumId,
            albumName = newAlbumWithArtistInformation.album.albumName,
            artistId = albumArtistToSave.artistId
        )

        // If so, we need to merge the two album.
        if (duplicateAlbum != null) mergeAlbums(from = duplicateAlbum, to = newAlbumWithArtistInformation.album)

        // We then need to update the musics of the album (new artist, album name and cover).
        val musicsFromAlbum = musicDataSource.getAllMusicFromAlbum(
            albumId = newAlbumWithArtistInformation.album.albumId
        )
        for (music in musicsFromAlbum) {
            val newMusic = music.copy(
                album = newAlbumWithArtistInformation.album.albumName,
                coverId = newAlbumWithArtistInformation.album.coverId,
                artist = newAlbumWithArtistInformation.artist.artistName
            )
            musicDataSource.insertMusic(newMusic)
            musicArtistDataSource.updateArtistOfMusic(
                musicId = music.musicId,
                newArtistId = albumArtistToSave.artistId
            )
        }

        // Finally, we can update the information of the album.
        albumDataSource.insertAlbum(newAlbumWithArtistInformation.album)

        // We check and delete the initial artist if it no longer possess songs.
        checkAndDeleteVerification.checkAndDeleteArtist(artistToCheck = initialArtist)
    }

    /**
     * Delete an album.
     */
    suspend fun delete(albumId: UUID) {
        val albumWithMusics = albumDataSource.getAlbumWithMusics(albumId = albumId)

        // We first delete the musics of the album.
        musicDataSource.deleteMusicFromAlbum(
            album = albumWithMusics.album.albumName,
            artist = albumWithMusics.artist!!.artistName
        )
        // We then delete the album
        albumDataSource.deleteAlbum(albumWithMusics.album)

        // Finally we can check if we can delete the artist of the deleted album.
        checkAndDeleteVerification.checkAndDeleteArtist(
            artistToCheck = albumWithMusics.artist,
        )
    }

    /**
     * Inserts or updates a new Album.
     */
    suspend fun insertAlbum(album: Album) = albumDataSource.insertAlbum(
        album = album
    )

    /**
     * Deletes an Album.
     */
    suspend fun deleteAlbum(album: Album) = albumDataSource.deleteAlbum(
        album = album
    )

    /**
     * Retrieves all Albums from an Artist.
     */
    suspend fun getAllAlbumsFromArtist(artistId: UUID): List<Album> =
        albumDataSource.getAllAlbumsFromArtist(
            artistId = artistId
        )

    /**
     * Retrieves an Album from its id.
     */
    suspend fun getAlbumFromId(albumId: UUID): Album? = albumDataSource.getAlbumFromId(
        albumId = albumId
    )

    /**
     * Retrieves a flow of an AlbumWithMusics from an album's id.
     */
    fun getAlbumWithMusicsAsFlow(albumId: UUID): Flow<AlbumWithMusics?> =
        albumDataSource.getAlbumWithMusicsAsFlow(
            albumId = albumId
        )

    /**
     * Retrieves an AlbumWithMusics from an album's id.
     */
    suspend fun getAlbumWithMusics(albumId: UUID): AlbumWithMusics =
        albumDataSource.getAlbumWithMusics(
            albumId = albumId
        )

    /**
     * Retrieves a flow of all Album, sorted bu name asc.
     */
    fun getAllAlbumsSortByNameAscAsFlow(): Flow<List<Album>> =
        albumDataSource.getAllAlbumsSortByNameAscAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by name asc.
     */
    fun getAllAlbumsWithMusicsSortByNameAscAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByNameDescAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by name desc.
     */
    fun getAllAlbumsWithMusicsSortByNameDescAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByNameDescAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by added date asc.
     */
    fun getAllAlbumsWithMusicsSortByAddedDateAscAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByAddedDateAscAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by date desc.
     */
    fun getAllAlbumsWithMusicsSortByAddedDateDescAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByAddedDateDescAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by nb played asc.
     */
    fun getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByNbPlayedAscAsFlow()

    /**
     * Retrieves a flow of all AlbumsWithMusics, sorted by nb played desc.
     */
    fun getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumsWithMusicsSortByNbPlayedDescAsFlow()

    /**
     * Retrieves all AlbumsWithArtist.
     */
    suspend fun getAllAlbumsWithArtist(): List<AlbumWithArtist> =
        albumDataSource.getAllAlbumsWithArtist()

    /**
     * Retrieves all AlbumsWithMusics.
     */
    suspend fun getAllAlbumsWithMusics(): List<AlbumWithMusics> =
        albumDataSource.getAllAlbumsWithMusics()

    /**
     * Retrieves a flow of all albums from the quick access.
     */
    fun getAllAlbumWithArtistFromQuickAccessAsFlow(): Flow<List<AlbumWithArtist>> =
        albumDataSource.getAllAlbumWithArtistFromQuickAccessAsFlow()

    /**
     * Tries to find a corresponding album from its name and its artist's id.
     */
    suspend fun getCorrespondingAlbum(albumName: String, artistId: UUID): Album? =
        albumDataSource.getCorrespondingAlbum(
            albumName = albumName,
            artistId = artistId
        )

    /**
     * Tries to find a duplicate album.
     */
    suspend fun getPossibleDuplicateAlbum(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): Album? = albumDataSource.getPossibleDuplicateAlbum(
        albumId = albumId,
        albumName = albumName,
        artistId = artistId
    )

    /**
     * Updates the cover of an album.
     */
    suspend fun updateAlbumCover(newCoverId: UUID, albumId: UUID) =
        albumDataSource.updateAlbumCover(
            newCoverId = newCoverId,
            albumId = albumId
        )

    /**
     * Updates the quick access status of an album.
     */
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, albumId: UUID) =
        albumDataSource.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            albumId = albumId
        )

    /**
     * Retrieves the number of albums sharing the same cover.
     */
    suspend fun getNumberOfAlbumsWithCoverId(coverId: UUID): Int =
        albumDataSource.getNumberOfAlbumsWithCoverId(
            coverId = coverId
        )

    /**
     * Retrieves the number of time an Album has been played.
     */
    suspend fun getNbPlayedOfAlbum(albumId: UUID): Int =
        albumDataSource.getNbPlayedOfAlbum(albumId = albumId)

    /**
     * Update the total of played time of an Album.
     */
    suspend fun updateNbPlayed(newNbPlayed: Int, albumId: UUID) = albumDataSource.updateNbPlayed(
        newNbPlayed = newNbPlayed,
        albumId = albumId
    )
}