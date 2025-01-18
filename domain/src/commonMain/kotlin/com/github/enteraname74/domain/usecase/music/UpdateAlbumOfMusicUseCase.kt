package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import java.util.*

class UpdateAlbumOfMusicUseCase(
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
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
        val legacyAlbum = getCorrespondingAlbumUseCase(music = legacyMusic)

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
                },
                artistId = artistId,
            )
            albumRepository.upsert(album = newMusicAlbum)
        }

        // We update the album of the music.
        musicRepository.upsert(
            music = legacyMusic.copy(
                albumId = newMusicAlbum.albumId,
            )
        )

        // We remove the legacy album if there is no music left in it.
        legacyAlbum?.let { album ->
            deleteAlbumIfEmptyUseCase(albumId = album.albumId)
        }
    }
}