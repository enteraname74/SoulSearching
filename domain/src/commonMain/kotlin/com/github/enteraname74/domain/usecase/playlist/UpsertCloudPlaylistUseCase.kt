package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.CloudPlaylist
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
import kotlin.uuid.Uuid

class UpsertCloudPlaylistUseCase(
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonMusicPlaylistUseCase: CommonMusicPlaylistUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
) {
    suspend operator fun invoke(
        cloudPlaylists: List<CloudPlaylist>,
        mergeMode: MergeMode,
    ) {

        val playlistToSaves: List<Playlist> = cloudPlaylists.map {
            val existingPlaylist: Playlist? = getExistingPlaylist(cloudPlaylistInfo = it.playlist)

            existingPlaylist?.merge(
                cloudPlaylistInfo = it.playlist,
                mergeMode = mergeMode,
            ) ?: it.toNewPlaylist()
        }

        commonPlaylistUseCase.upsertAll(playlists = playlistToSaves, keepUpdatedAt = true)

        playlistToSaves.forEach { playlist ->
            cloudPlaylists.find { it.playlist.id == playlist.remoteId }?.musicIds?.let { musicRemoteIds ->
                val safeMusicIds: List<Uuid> = commonMusicUseCase.getIdsFromRemoteIds(
                    remoteIds = musicRemoteIds,
                )
                commonMusicPlaylistUseCase.upsertAll(
                    musicPlaylists = safeMusicIds.map {
                        MusicPlaylist(
                            musicId = it,
                            playlistId = playlist.playlistId,
                        )
                    },
                    keepUpdatedAt = true,
                )

            }
        }
    }

    private suspend fun getExistingPlaylist(
        cloudPlaylistInfo: CloudPlaylist.Info,
    ): Playlist? =
        if (cloudPlaylistInfo.isFavorite) {
            commonPlaylistUseCase.getFavorite()
        } else {
            commonPlaylistUseCase.getFromRemoteId(remoteId = cloudPlaylistInfo.id)
                ?: commonPlaylistUseCase.getFromName(name = cloudPlaylistInfo.name)
        }
}