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
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
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
    
    suspend fun update(
        newAlbumWithArtistInformation: AlbumWithArtist
    ) {
        val initialAlbum = albumDataSource.getAlbumFromId(
            albumId = state.value.albumWithMusics.album.albumId
        )!!

        val initialArtist = artistDataSource.getArtistFromId(
            artistId = state.value.albumWithMusics.artist!!.artistId
        )
        var currentArtist = initialArtist

        if (newAlbumWithArtistInformation.artist!!.artistName != initialArtist!!.artistName) {
            // On cherche le nouvel artiste correspondant :
            var newArtist = artistDataSource.getArtistFromInfo(
                artistName = newAlbumWithArtistInformation.artist.artistName
            )
            // Si ce nouvel artiste n'existe pas, on le crée :
            if (newArtist == null) {
                newArtist = Artist(
                    artistName = newAlbumWithArtistInformation.artist.artistName,
                    coverId = newAlbumWithArtistInformation.album.coverId
                )
                artistDataSource.insertArtist(
                    artist = newArtist
                )
            }
            // On met à jour le lien vers l'artiste :
            albumArtistDataSource.updateArtistOfAlbum(
                albumId = newAlbumWithArtistInformation.album.albumId,
                newArtistId = newArtist.artistId
            )

            val duplicateAlbum = albumDataSource.getPossibleDuplicateAlbum(
                albumId = newAlbumWithArtistInformation.album.albumId,
                albumName = newAlbumWithArtistInformation.album.albumName,
                artistId = newArtist.artistId
            )

            if (duplicateAlbum != null) {
                /*
                 Un album a le même nom d'album et d'artiste !
                 On redirige les musiques de l'album dupliqué :
                 */
                musicAlbumDataSource.updateMusicsAlbum(
                    newAlbumId = newAlbumWithArtistInformation.album.albumId,
                    legacyAlbumId = duplicateAlbum.albumId
                )
                // On supprime l'ancien album :
                albumArtistDataSource.deleteAlbumFromArtist(
                    albumId = duplicateAlbum.albumId
                )
                albumDataSource.deleteAlbum(
                    album = duplicateAlbum
                )
            }
            currentArtist = newArtist
        } else if (newAlbumWithArtistInformation.album.albumName != initialAlbum.albumName) {
            val duplicateAlbum = albumDataSource.getPossibleDuplicateAlbum(
                albumId = newAlbumWithArtistInformation.album.albumId,
                albumName = newAlbumWithArtistInformation.album.albumName,
                artistId = newAlbumWithArtistInformation.artist.artistId
            )

            if (duplicateAlbum != null) {
                /*
                 Un album a le même nom d'album et d'artiste !
                 On redirige les musiques de l'album dupliqué :
                 */
                musicAlbumDataSource.updateMusicsAlbum(
                    newAlbumId = newAlbumWithArtistInformation.album.albumId,
                    legacyAlbumId = duplicateAlbum.albumId
                )
                // On supprime l'ancien album :
                albumArtistDataSource.deleteAlbumFromArtist(
                    albumId = duplicateAlbum.albumId
                )
                albumDataSource.deleteAlbum(
                    album = duplicateAlbum
                )
            }
        }

        // On met à jour les musiques de l'album :
        val musicsFromAlbum = musicDataSource.getAllMusicFromAlbum(
            albumId = newAlbumWithArtistInformation.album.albumId
        )
        for (music in musicsFromAlbum) {
            // On modifie les infos de chaque musique
            val newMusic = music.copy(
                album = newAlbumWithArtistInformation.album.albumName,
                coverId = newAlbumWithArtistInformation.album.coverId,
                artist = newAlbumWithArtistInformation.artist.artistName
            )
            musicDataSource.insertMusic(newMusic)
            // Ainsi que leur liens :
            musicArtistDataSource.updateArtistOfMusic(
                musicId = music.musicId,
                newArtistId = currentArtist!!.artistId
            )

            playbackManager.updateMusic(newMusic)

            playbackManager.currentMusic?.let {
                if (it.musicId.compareTo(music.musicId) == 0) {
                    playbackManager.updateCover(
                        cover = state.value.albumCover
                    )
                }
            }
        }

        // On modifie notre album :
        albumDataSource.insertAlbum(newAlbumWithArtistInformation.album)

        // On vérifie si l'ancien artiste possède encore des musiques :
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