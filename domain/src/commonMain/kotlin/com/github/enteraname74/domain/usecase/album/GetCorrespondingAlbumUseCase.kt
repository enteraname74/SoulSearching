package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

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
    ): Album? {
        val allAlbums: List<Album> = albumRepository.getAll().first()
        return allAlbums
            .firstOrNull {
                it.albumName == albumName && it.artist.artistId == artistId
            }
    }

    /**
     * Tries to retrieve the corresponding album of a music and its artist name.
     */
    suspend operator fun invoke(
        albumName: String,
        artistName: String,
    ): Album? {
        val allAlbums: List<Album> = albumRepository.getAll().first()
        return allAlbums
            .firstOrNull {
                it.albumName == albumName && it.artist.artistName == artistName
            }
    }

    suspend fun withMusics(
        musicId: UUID
    ): AlbumWithMusics? {
        val albumId: UUID = musicRepository.getFromId(musicId).firstOrNull()?.album?.albumId ?: return null
        return albumRepository.getAlbumWithMusics(albumId = albumId).first()
    }
}