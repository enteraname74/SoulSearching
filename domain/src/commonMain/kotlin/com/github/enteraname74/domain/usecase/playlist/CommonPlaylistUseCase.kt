package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.PlaylistWithMusicsNumber
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class CommonPlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    suspend fun deleteAll(playlistIds: List<UUID>) {
        playlistRepository.deleteAll(playlistIds)
    }

    suspend fun delete(playlist: Playlist) {
        playlistRepository.delete(playlist = playlist)
    }

    fun getAll(): Flow<List<Playlist>> =
        playlistRepository.getAll()

    fun getAllWithMusics(): Flow<List<PlaylistWithMusics>> =
        playlistRepository.getAllPlaylistWithMusics()

    fun getAllFromQuickAccess(): Flow<List<PlaylistWithMusicsNumber>> =
        playlistRepository.getAllPlaylistWithMusics().map { list ->
            list
                .filter { it.playlist.isInQuickAccess }
                .map { it.toPlaylistWithMusicsNumber() }
        }

    fun getFavorite(): Flow<PlaylistWithMusics?> =
        playlistRepository.getAllPlaylistWithMusics().map { list ->
            list.firstOrNull { it.playlist.isFavorite }
        }

    fun getWithMusics(playlistId: UUID): Flow<PlaylistWithMusics?> =
        playlistRepository.getPlaylistWithMusics(playlistId)

    suspend fun incrementNbPlayed(playlistId: UUID) {
        val playlist: Playlist = playlistRepository.getFromId(playlistId).first() ?: return
        playlistRepository.upsert(
            playlist.copy(
                nbPlayed = playlist.nbPlayed + 1
            )
        )
    }

    suspend fun upsertAll(playlists: List<Playlist>) {
        playlistRepository.upsertAll(playlists)
    }

    suspend fun upsert(playlist: Playlist) {
        playlistRepository.upsert(
            playlist = playlist,
        )
    }
}