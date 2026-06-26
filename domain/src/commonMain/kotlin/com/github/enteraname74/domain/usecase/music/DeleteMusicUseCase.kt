package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.cloud.HasValidCloudInformationUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class DeleteMusicUseCase(
    private val musicRepository: MusicRepository,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
    private val hasValidCloudInformationUseCase: HasValidCloudInformationUseCase,
) {
    suspend operator fun invoke(musicId: UUID) {
        val musicToRemove: Music = musicRepository.getFromId(musicId = musicId).first() ?: return
        deleteMusic(music = musicToRemove)
    }

    suspend fun fromUnselectedFolders(ids: List<UUID>) {
        val remoteIds: List<String> = musicRepository.getRemoteIdsFromIds(ids)
        musicRepository.deleteAllFromUnselectedFolders()
        deleteRemoteIfPossible(remoteIds)
    }

    suspend operator fun invoke(musicIds: List<UUID>) {
        val musics: List<Music> = musicRepository.getFromIds(musicIds).firstOrNull() ?: return

        musicRepository.deleteAll(musicIds)

        val albumIdsToCheck: Set<UUID> = musics
            .map { it.album.albumId }
            .toSet()
        albumIdsToCheck.forEach { deleteAlbumIfEmptyUseCase(albumId = it) }

        val artistIdsToCheck: Set<UUID> = musics
            .flatMap { it.artists }
            .map { it.artistId }
            .toSet()
        artistIdsToCheck.forEach { commonArtistUseCase.deleteIfEmpty(artistId = it) }

        deleteRemoteIfPossible(remoteIds = musics.mapNotNull { it.remoteId })
    }

    private suspend fun deleteMusic(music: Music) {
        musicRepository.delete(music = music)

        deleteAlbumIfEmptyUseCase(albumId = music.album.albumId)
        music.artists.forEach { artist ->
            commonArtistUseCase.deleteIfEmpty(artistId = artist.artistId)
        }
        music.remoteId?.let { deleteRemoteIfPossible(remoteIds = listOf(it)) }
    }

    suspend operator fun invoke(music: Music) {
        deleteMusic(music = music)
    }

    suspend fun deleteSharedMusics() {
        musicRepository.deleteSharedPlayedListMusics()
        deleteEmptyAlbumsAndArtistsUseCase()
    }

    suspend fun deleteRemoteIfPossible(remoteIds: List<String>) {
        if (hasValidCloudInformationUseCase().firstOrNull() == true) {
            println("CLUELESS -- Deleting remotely: $remoteIds -- ${musicRepository.deleteRemotely(remoteIds)}")
        }
    }
}