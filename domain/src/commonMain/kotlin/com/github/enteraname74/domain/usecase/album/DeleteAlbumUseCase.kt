package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import kotlinx.coroutines.flow.first
import java.util.UUID

class DeleteAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
) {
    suspend operator fun invoke(albumId: UUID) {
        val albumWithMusics = albumRepository.getAlbumWithMusics(albumId = albumId).first() ?: return

        // We first delete the musics of the album.
        if (albumWithMusics.musics.isNotEmpty()) {
            musicRepository.deleteAllMusicOfAlbum(
                album = albumWithMusics.album.albumName,
                artist = albumWithMusics.artist!!.artistName
            )
        }
        // We then delete the album
        albumRepository.delete(albumWithMusics.album)

        // Finally we can check if we can delete the artist of the deleted album.
        albumWithMusics.artist?.let {
            deleteArtistIfEmptyUseCase(
                artistId = it.artistId,
            )
        }
    }
}