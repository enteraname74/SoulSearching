package com.github.enteraname74.domain.usecase.album

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.util.CheckAndDeleteVerification
import kotlinx.coroutines.flow.first

class UpdateAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val checkAndDeleteVerification: CheckAndDeleteVerification,
    private val getDuplicatedAlbumUseCase: GetDuplicatedAlbumUseCase
) {
    suspend operator fun invoke(
        newAlbumWithArtistInformation: AlbumWithArtist,
    ) {
        if (newAlbumWithArtistInformation.artist == null) return

        val initialArtist: Artist = artistRepository.getFromId(
            artistId = newAlbumWithArtistInformation.artist.artistId
        ).first() ?: return


        var albumArtistToSave = initialArtist
        if (newAlbumWithArtistInformation.artist.artistName != initialArtist.artistName) {
            // We first try to find if there is an existing artist with the new artist name.
            var newArtist = artistRepository.getFromName(
                artistName = newAlbumWithArtistInformation.artist.artistName
            )
            // If this artist doesn't exist, we create it.
            if (newArtist == null) {
                newArtist = Artist(
                    artistName = newAlbumWithArtistInformation.artist?.artistName.orEmpty(),
                    coverId = newAlbumWithArtistInformation.album.coverId
                )
                artistRepository.upsert(
                    artist = newArtist
                )
            }
            // We update the link between the album and its artist.
            albumArtistRepository.update(
                albumId = newAlbumWithArtistInformation.album.albumId,
                newArtistId = newArtist.artistId
            )
            albumArtistToSave = newArtist
        }

        // We then check if there is an album with the same name and artist that already exist.
        val duplicateAlbum = getDuplicatedAlbumUseCase(
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
     * Merge two albums together.
     * @param from the album to put to the "to" album.
     * @param to the album that will receive the merge ("from" param)/
     */
    private suspend fun mergeAlbums(from: Album, to: Album) {
        // We update the link of the musics of the duplicated album to the new album id.
        musicAlbumRepository.updateMusicsAlbum(
            newAlbumId = to.albumId,
            legacyAlbumId = from.albumId
        )
        // We remove the previous album.
        albumArtistRepository.delete(
            albumId = from.albumId
        )
        albumRepository.delete(
            album = from
        )
    }
}