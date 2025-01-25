package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
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
    private suspend fun deleteMusic(music: Music): SoulResult<String> {
        val artists: List<Artist> = getArtistsOfMusicUseCase(musicId = music.musicId).firstOrNull() ?: emptyList()
        val album: Album? = getCorrespondingAlbumUseCase(music = music)

        val musicDeletionResult: SoulResult<String> = musicRepository.delete(music = music)
        if (musicDeletionResult.isError()) return musicDeletionResult

        album?.let { musicAlbum ->
            val result = deleteAlbumIfEmptyUseCase(albumId = musicAlbum.albumId)
            if (result.isError()) return result
        }
        artists.forEach { musicArtist ->
            val result = deleteArtistIfEmptyUseCase(artistId = musicArtist.artistId)
            if (result.isError()) return result
        }

        return SoulResult.Success("")
    }

    suspend operator fun invoke(music: Music): SoulResult<String> =
        deleteMusic(music = music)
}