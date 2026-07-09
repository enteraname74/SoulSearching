package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.CloudPlaylist
import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.PlaylistRepository

class UploadPlaylistToCloudUseCase(
    private val playlistRepository: PlaylistRepository,
    private val upsertCloudPlaylistUseCase: UpsertCloudPlaylistUseCase,
) {
    suspend operator fun invoke(
        playlistWithMusics: PlaylistWithMusics,
        mergeMode: MergeMode,
    ): SoulResult<Unit> = SoulResult.runCatching {
        val cloudPlaylist: CloudPlaylist = playlistRepository.uploadToCloud(playlistWithMusics)
        println("CLUELESS -- got playlist: $cloudPlaylist")
        upsertCloudPlaylistUseCase(
            cloudPlaylists = listOf(cloudPlaylist),
            mergeMode = mergeMode,
        )
    }
}