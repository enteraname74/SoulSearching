package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class UpdateAlbumNbPlayedUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: UUID) {
        val album: Album = albumRepository.getFromId(albumId = albumId).first() ?: return
        println("GOT NEW NB PLAYED: ${album.nbPlayed + 1}")
        albumRepository.upsert(
            album = album.copy(
                nbPlayed = album.nbPlayed + 1,
            )
        )
    }
}