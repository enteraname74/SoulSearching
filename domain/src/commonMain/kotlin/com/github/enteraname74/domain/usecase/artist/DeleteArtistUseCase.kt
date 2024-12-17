package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.usecase.music.DeleteAllMusicsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

class DeleteArtistUseCase(
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val deleteAllMusicsUseCase: DeleteAllMusicsUseCase,
) {
    suspend operator fun invoke(artistWithMusics: ArtistWithMusics) {
        // We first delete the songs of the artist.
        deleteAllMusicsUseCase(
            ids = artistWithMusics.musics.map { it.musicId }
        )
        // We then delete all the albums of the artist.
        val albumsToDelete = albumRepository.getAlbumsOfArtist(
            artistId = artistWithMusics.artist.artistId
        ).first()

        albumRepository.deleteAll(
            ids = albumsToDelete.map { it.albumId }
        )

        // And we finally delete the artist.
        artistRepository.delete(artistWithMusics.artist)
    }

    suspend operator fun invoke(artistId: UUID) {
        artistRepository.getArtistWithMusics(artistId).firstOrNull()?.let {
            artistRepository.delete(it.artist)
        }
    }
}