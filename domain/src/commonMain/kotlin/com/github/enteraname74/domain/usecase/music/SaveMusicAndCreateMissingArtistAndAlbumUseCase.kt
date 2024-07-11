package com.github.enteraname74.domain.usecase.music

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import java.util.*

class SaveMusicAndCreateMissingArtistAndAlbumUseCase(
    private val imageCoverRepository: ImageCoverRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
) {
    suspend operator fun invoke(
        music: Music,
        musicCover: ImageBitmap?,
        albumId: UUID,
        artistId: UUID
    ) {
        val coverId = UUID.randomUUID()
        if (musicCover != null) {
            music.coverId = coverId
            imageCoverRepository.upsert(
                ImageCover(
                    coverId = coverId,
                    cover = musicCover
                )
            )
        }

        albumRepository.upsert(
            Album(
                coverId = if (musicCover != null) coverId else null,
                albumId = albumId,
                albumName = music.album
            )
        )
        artistRepository.upsert(
            Artist(
                coverId = if (musicCover != null) coverId else null,
                artistId = artistId,
                artistName = music.artist
            )
        )
        albumArtistRepository.upsert(
            AlbumArtist(
                albumId = albumId,
                artistId = artistId
            )
        )
    }
}