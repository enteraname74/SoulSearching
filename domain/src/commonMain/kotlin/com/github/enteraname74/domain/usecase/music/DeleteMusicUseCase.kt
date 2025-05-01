package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

class DeleteMusicUseCase(
    private val musicRepository: MusicRepository,
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
) {
    suspend operator fun invoke(musicId: UUID) {
        val musicToRemove: Music = musicRepository.getFromId(musicId = musicId).first() ?: return
        deleteMusic(music = musicToRemove)
    }

    private suspend fun deleteMusic(music: Music) {
        val artists: List<Artist> = commonArtistUseCase.getArtistsOfMusic(
            music = music,
            withAlbumArtist = true,
        ).firstOrNull() ?: emptyList()
        val album: Album? = getCorrespondingAlbumUseCase(musicId = music.musicId)

        musicRepository.delete(music = music)

        album?.let { musicAlbum ->
            deleteAlbumIfEmptyUseCase(albumId = musicAlbum.albumId)
        }
        artists.forEach { musicArtist ->
            commonArtistUseCase.deleteIfEmpty(artistId = musicArtist.artistId)
        }
    }

    suspend operator fun invoke(music: Music) {
        deleteMusic(music = music)
    }
}