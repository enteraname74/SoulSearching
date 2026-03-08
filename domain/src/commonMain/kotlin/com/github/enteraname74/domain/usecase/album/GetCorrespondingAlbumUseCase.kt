package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class GetCorrespondingAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
) {

    /**
     * Tries to retrieve the corresponding album of a music and its artist id.
     */
    suspend operator fun invoke(
        albumName: String,
        artistId: UUID
    ): Album? =
        albumRepository.getFromArtistId(
            albumName = albumName,
            artistId = artistId,
        )

    /**
     * Tries to retrieve the corresponding album of a music and its artist name.
     */
    suspend operator fun invoke(
        albumName: String,
        artistName: String,
    ): Album? =
        albumRepository.getFromInformation(
            albumName = albumName,
            artistName = artistName,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun withMusics(
        musicId: UUID
    ): Flow<AlbumWithMusics?> =
        musicRepository.getFromId(musicId).flatMapLatest { music ->
            music?.let {
                albumRepository.getAlbumWithMusics(albumId = music.album.albumId)
            } ?: flowOf()
        }
}