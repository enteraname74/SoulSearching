package com.github.enteraname74.domain.usecase.user

import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.UserRepository
import com.github.enteraname74.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase

class LogoutFromCloudUseCase(
    private val userRepository: UserRepository,
    private val cloudPreferencesRepository: CloudPreferencesRepository,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
    private val musicRepository: MusicRepository,
) {

    /**
     * On logout from cloud, we need to ensure the following things:
     * - no last sync date left
     * - no musics from the cloud
     */
    suspend operator fun invoke() {
        userRepository.logout()
        cloudPreferencesRepository.clearLastSyncMillis()

        /*
        We need to clear the musics from the cloud.
        First, removing all remote ids.
        Then, clear not existing files.
        Finally, check for album/artist deletion
         */
        musicRepository.deleteAllRemoteIds()
        musicRepository.deleteNotExisting()
        deleteEmptyAlbumsAndArtistsUseCase()
    }
}