package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsOfMusicUseCase
import kotlinx.coroutines.flow.firstOrNull

class DeleteMusicUseCase(
    private val musicRepository: MusicRepository,
    private val getArtistsOfMusicUseCase: GetArtistsOfMusicUseCase,
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
) {
    private suspend fun deleteLocal(music: Music): SoulResult<Unit> {
        val artists: List<Artist> = getArtistsOfMusicUseCase(musicId = music.musicId).firstOrNull() ?: emptyList()
        val album: Album? = getCorrespondingAlbumUseCase(music = music)

        musicRepository.delete(music = music)

        album?.let { musicAlbum ->
            deleteAlbumIfEmptyUseCase(albumId = musicAlbum.albumId)
        }
        artists.forEach { musicArtist ->
            deleteArtistIfEmptyUseCase(artistId = musicArtist.artistId)
        }

        return SoulResult.ofSuccess()
    }

    suspend operator fun invoke(music: Music): SoulResult<Unit> =
        when(music.dataMode) {
            DataMode.Local -> deleteLocal(music)
            DataMode.Cloud -> musicRepository.delete(music)
        }
}