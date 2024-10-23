package com.github.enteraname74.soulsearching.features.filemanager.usecase

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.repository.*
import com.github.enteraname74.domain.usecase.album.GetDuplicatedAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.soulsearching.features.filemanager.util.MusicFileUpdater
import kotlinx.coroutines.flow.first

class UpdateAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicRepository: MusicRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val getDuplicatedAlbumUseCase: GetDuplicatedAlbumUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val musicFileUpdater: MusicFileUpdater,
) {
    suspend operator fun invoke(
        newAlbumWithArtistInformation: AlbumWithArtist,
    ) {
        if (newAlbumWithArtistInformation.artist == null) return

        val initialArtist: Artist = artistRepository.getFromId(
            artistId = newAlbumWithArtistInformation.artist!!.artistId
        ).first() ?: return


        var albumArtistToSave = initialArtist
        if (newAlbumWithArtistInformation.artist!!.artistName != initialArtist.artistName) {
            // We first try to find if there is an existing artist with the new artist name.
            var newArtist = artistRepository.getFromName(
                artistName = newAlbumWithArtistInformation.artist!!.artistName
            )
            // If this artist doesn't exist, we create it.
            if (newArtist == null) {
                newArtist = Artist(
                    artistName = newAlbumWithArtistInformation.artist!!.artistName,
                    cover = newAlbumWithArtistInformation.album.cover,
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
        val musicsFromAlbum = musicRepository.getAllMusicFromAlbum(
            albumId = newAlbumWithArtistInformation.album.albumId
        )

        for (music in musicsFromAlbum) {
            val newMusic = music.copy(
                album = newAlbumWithArtistInformation.album.albumName,
                cover = (music.cover as? Cover.FileCover)?.copy(
                    fileCoverId = (newAlbumWithArtistInformation.album.cover as? Cover.FileCover)?.fileCoverId
                        ?: (music.cover as? Cover.FileCover)?.fileCoverId
                ) ?: music.cover,
                artist = newAlbumWithArtistInformation.artist!!.artistName
            )
            musicRepository.upsert(newMusic)
            musicFileUpdater.updateMusic(music = newMusic)
            musicArtistRepository.updateArtistOfMusic(
                musicId = music.musicId,
                newArtistId = albumArtistToSave.artistId
            )
        }

        // We adapt the quick access status with the potential duplicate album.
        val albumToSave = newAlbumWithArtistInformation.album.copy(
            isInQuickAccess = newAlbumWithArtistInformation.album.isInQuickAccess
                    || duplicateAlbum?.isInQuickAccess == true
        )

        // Finally, we can update the information of the album.
        albumRepository.upsert(albumToSave)

        // We check and delete the initial artist if it no longer possess songs.
        deleteArtistIfEmptyUseCase(artistId = initialArtist.artistId)
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