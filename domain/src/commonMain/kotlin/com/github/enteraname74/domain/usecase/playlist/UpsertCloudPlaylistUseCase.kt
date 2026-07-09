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
        cloudPlaylist: CloudPlaylist,
        mergeMode: MergeMode,
    ) {
        val existingPlaylist: Playlist? = getExistingPlaylist(cloudPlaylistInfo = cloudPlaylist.playlist)

        val playlistToSave: Playlist = existingPlaylist?.merge(
            cloudPlaylistInfo = cloudPlaylist.playlist,
            mergeMode = mergeMode,
        ) ?: cloudPlaylist.toNewPlaylist()

        commonPlaylistUseCase.upsert(playlistToSave)
        val safeMusicIds: List<Uuid> = commonMusicUseCase.getIdsFromRemoteIds(
            remoteIds = cloudPlaylist.musicIds,
        )
        commonMusicPlaylistUseCase.upsertAll(
            musicPlaylists = safeMusicIds.map {
                MusicPlaylist(
                    musicId = it,
                    playlistId = playlistToSave.playlistId,
                )
            }
        )
    }

    private suspend fun getExistingPlaylist(
        cloudPlaylistInfo: CloudPlaylist.Info,
    ): Playlist? =
        if (cloudPlaylistInfo.isFavorite) {
            commonPlaylistUseCase.getFavorite()
        } else {
            commonPlaylistUseCase.getFromName(name = cloudPlaylistInfo.name)
        }
}