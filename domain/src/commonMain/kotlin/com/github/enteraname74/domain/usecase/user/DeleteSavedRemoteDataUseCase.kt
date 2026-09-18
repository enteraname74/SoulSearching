package com.github.enteraname74.domain.usecase.user

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob

// TODO CLOUD: What to do with remote playlist only that are now empty? How can we distinct them from local playlists to delete them?
class DeleteSavedRemoteDataUseCase(
    private val cloudPreferencesRepository: CloudPreferencesRepository,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
    private val musicRepository: MusicRepository,
    private val playerRepository: PlayerRepository,
    private val cloudBackgroundSyncJob: CloudBackgroundSyncJob,
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(): SoulResult<Unit> = SoulResult.runCatching {
        cloudBackgroundSyncJob.cancelIfNeeded()
        cloudPreferencesRepository.clearLastSyncMillis()

        /*
        We need to clear the musics from the cloud.
        First, removing all remote ids.
        Then, clear not existing files.
        Finally, check for album/artist deletion
         */
        musicRepository.deleteAllRemoteFields()
        musicRepository.deleteNotExisting()
        playlistRepository.deleteAllRemoteFields()

        deleteEmptyAlbumsAndArtistsUseCase()

        playerRepository.deleteAllSharedPlayedListPreviews()
    }
}