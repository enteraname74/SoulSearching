package com.github.enteraname74.soulsearching.domain.usecase.user

import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.repository.UserRepository

class LogoutFromCloudUseCase(
    private val userRepository: UserRepository,
    private val deleteSavedRemoteDataUseCase: DeleteSavedRemoteDataUseCase,
) {

    /**
     * On logout from cloud, we need to ensure the following things:
     * - no last sync date left
     * - no musics from the cloud
     */
    suspend operator fun invoke(): SoulResult<Unit> = SoulResult.runCatching {
        userRepository.logout()
        deleteSavedRemoteDataUseCase()
    }
}
