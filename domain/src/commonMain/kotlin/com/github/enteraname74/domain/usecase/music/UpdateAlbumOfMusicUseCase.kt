package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import java.util.*

class UpdateAlbumOfMusicUseCase(
    private val albumRepository: AlbumRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
) {
    /**
     * Update the album of a music.
     * @param legacyMusic the legacy music information to update.
     * @param artistId the id of the music's artist.
     * @param newAlbumName the new album name of the music.
     */
    suspend operator fun invoke(
        legacyMusic: Music,
        artistId: UUID,
        newAlbumName: String
    ) {
        val legacyAlbum: Album? = getCorrespondingAlbumUseCase(musicId = legacyMusic.musicId)

        var newMusicAlbum = getCorrespondingAlbumUseCase(
            albumName = newAlbumName,
            artistId = artistId
        )

        // If the album name is not corresponding to an existing album, we create it.
        if (newMusicAlbum == null) {
            newMusicAlbum = Album(
                albumName = newAlbumName,
                cover = (legacyMusic.cover as? Cover.CoverFile)?.fileCoverId?.let {
                    Cover.CoverFile(fileCoverId = it)
                }
            )
            albumRepository.upsert(album = newMusicAlbum)

            // We link the new album to the music's artist.
            albumArtistRepository.upsert(
                AlbumArtist(
                    albumId = newMusicAlbum.albumId,
                    artistId = artistId
                )
            )
        }

        // We update the album of the music.
        musicAlbumRepository.updateAlbumOfMusic(
            musicId = legacyMusic.musicId,
            newAlbumId = newMusicAlbum.albumId
        )

        // We remove the legacy album if there is no music left in it.
        legacyAlbum?.let { album ->
            deleteAlbumIfEmptyUseCase(albumId = album.albumId)
        }
    }
}