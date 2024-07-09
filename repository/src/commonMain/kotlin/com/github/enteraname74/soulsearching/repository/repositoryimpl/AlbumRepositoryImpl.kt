package com.github.enteraname74.soulsearching.repository.repositoryimpl

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.util.CheckAndDeleteVerification
import com.github.enteraname74.domain.util.MusicFileUpdater
import com.github.enteraname74.soulsearching.repository.datasource.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*

/**
 * Repository of an Album.
 */
class AlbumRepositoryImpl(
    private val albumDataSource: AlbumDataSource,
    private val musicDataSource: MusicDataSource,
    private val artistDataSource: ArtistDataSource,
    private val albumArtistDataSource: AlbumArtistDataSource,
    private val musicAlbumDataSource: MusicAlbumDataSource,
    private val musicArtistDataSource: MusicArtistDataSource,
    private val imageCoverDataSource: ImageCoverDataSource,
    private val checkAndDeleteVerification: CheckAndDeleteVerification,
): AlbumRepository {

    private val musicFileUpdater: MusicFileUpdater = MusicFileUpdater()

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
        albumArtistDataSource.delete(
            albumId = from.albumId
        )
        albumDataSource.delete(
            album = from
        )
    }

    /**
     * Update an album with new information.
     */
    override suspend fun update(
        newAlbumWithArtistInformation: AlbumWithArtist
    ) {
        val initialArtist = artistDataSource.getFromId(
            artistId = newAlbumWithArtistInformation.artist!!.artistId
        ) ?: return


        var albumArtistToSave = initialArtist
        if (newAlbumWithArtistInformation.artist?.artistName != initialArtist.artistName) {
            // We first try to find if there is an existing artist with the new artist name.
            var newArtist = artistDataSource.getFromName(
                artistName = newAlbumWithArtistInformation.artist?.artistName.orEmpty()
            )
            // If this artist doesn't exist, we create it.
            if (newArtist == null) {
                newArtist = Artist(
                    artistName = newAlbumWithArtistInformation.artist?.artistName.orEmpty(),
                    coverId = newAlbumWithArtistInformation.album.coverId
                )
                artistDataSource.upsert(
                    artist = newArtist
                )
            }
            // We update the link between the album and its artist.
            albumArtistDataSource.update(
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

        var albumCover: ImageBitmap? = null
        newAlbumWithArtistInformation.album.coverId?.let { coverId ->
            albumCover = imageCoverDataSource.getCoverOfElement(coverId = coverId)?.cover
        }

        for (music in musicsFromAlbum) {
            val newMusic = music.copy(
                album = newAlbumWithArtistInformation.album.albumName,
                coverId = newAlbumWithArtistInformation.album.coverId,
                artist = newAlbumWithArtistInformation.artist?.artistName.orEmpty()
            )
            musicDataSource.upsert(newMusic)
            musicFileUpdater.updateMusic(
                music = newMusic,
                cover = albumCover
            )
            musicArtistDataSource.updateArtistOfMusic(
                musicId = music.musicId,
                newArtistId = albumArtistToSave.artistId
            )
        }

        // Finally, we can update the information of the album.
        albumDataSource.upsert(newAlbumWithArtistInformation.album)

        // We check and delete the initial artist if it no longer possess songs.
        checkAndDeleteVerification.checkAndDeleteArtist(artistToCheck = initialArtist)
    }

    /**
     * Delete an album.
     */
    override suspend fun delete(albumId: UUID) {
        val albumWithMusics = albumDataSource.getAlbumWithMusics(albumId = albumId).first() ?: return

        // We first delete the musics of the album.
        musicDataSource.deleteMusicFromAlbum(
            album = albumWithMusics.album.albumName,
            artist = albumWithMusics.artist!!.artistName
        )
        // We then delete the album
        albumDataSource.delete(albumWithMusics.album)

        // Finally we can check if we can delete the artist of the deleted album.
        checkAndDeleteVerification.checkAndDeleteArtist(
            artistToCheck = albumWithMusics.artist!!,
        )
    }

    override suspend fun upsert(album: Album) = albumDataSource.upsert(
        album = album
    )

    override fun getAlbumsOfArtist(artistId: UUID): Flow<List<Album>> = albumDataSource.getAlbumsOfArtist(
        artistId = artistId
    )

    override fun getFromId(albumId: UUID): Flow<Album?> = albumDataSource.getFromId(
        albumId = albumId
    )

    override fun getAlbumWithMusics(albumId: UUID): Flow<AlbumWithMusics?> =
        albumDataSource.getAlbumWithMusics(
            albumId = albumId
        )

    override fun getAll(): Flow<List<Album>> =
        albumDataSource.getAll()

    override fun getAllAlbumWithMusics(): Flow<List<AlbumWithMusics>> =
        albumDataSource.getAllAlbumWithMusics()

    override suspend fun getAllAlbumsWithArtist(): List<AlbumWithArtist> =
        albumDataSource.getAllAlbumsWithArtist()
}