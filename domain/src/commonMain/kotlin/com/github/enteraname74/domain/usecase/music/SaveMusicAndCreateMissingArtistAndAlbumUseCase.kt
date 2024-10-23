package com.github.enteraname74.domain.usecase.music

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.CoverRepository
import java.util.*

class SaveMusicAndCreateMissingArtistAndAlbumUseCase(
    private val coverRepository: CoverRepository,
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
//        val coverId = UUID.randomUUID()
//        if (musicCover != null) {
//            music.coverId = coverId
//            coverRepository.upsert(
//                ImageCover(
//                    coverId = coverId,
//                    cover = musicCover
//                )
//            )
//        }
//
//        albumRepository.upsert(
//            Album(
//                coverId = if (musicCover != null) coverId else null,
//                albumId = albumId,
//                albumName = music.album
//            )
//        )
//        artistRepository.upsert(
//            Artist(
//                coverId = if (musicCover != null) coverId else null,
//                artistId = artistId,
//                artistName = music.artist
//            )
//        )
//        albumArtistRepository.upsert(
//            AlbumArtist(
//                albumId = albumId,
//                artistId = artistId
//            )
//        )
    }
}