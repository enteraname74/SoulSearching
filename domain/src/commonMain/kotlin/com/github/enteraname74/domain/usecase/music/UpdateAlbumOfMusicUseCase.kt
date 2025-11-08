package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase

class UpdateAlbumOfMusicUseCase(
    private val musicRepository: MusicRepository,
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
) {
    /**
     * Update the album of a music.
     * @param legacyMusic the legacy music information to update.
     * @param artist the id of the .
     * @param newAlbum the new album name of the music.
     */
    suspend operator fun invoke(
        legacyMusic: Music,
        artist: Artist,
        newAlbum: Album,
    ) {
        val newMusicAlbum = getCorrespondingAlbumUseCase(
            albumName = newAlbum.albumName,
            artistId = artist.artistId,
        ) ?: Album(
            albumName = newAlbum.albumName,
            cover = (legacyMusic.cover as? Cover.CoverFile)?.fileCoverId?.let {
                Cover.CoverFile(fileCoverId = it)
            },
            artist = artist,
        )

        // We update the album of the music.
        musicRepository.upsert(
            music = legacyMusic.copy(
                album = newMusicAlbum,
            ),
        )

        // We remove the legacy album if there is no music left in it.
        deleteAlbumIfEmptyUseCase(albumId = legacyMusic.album.albumId)
    }
}