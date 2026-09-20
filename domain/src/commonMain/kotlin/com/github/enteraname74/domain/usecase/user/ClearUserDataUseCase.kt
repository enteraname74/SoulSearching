package com.github.enteraname74.domain.usecase.user

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.UserRepository
import com.github.enteraname74.domain.usecase.music.ObserveDataChangedForCloudSync

class ClearUserDataUseCase(
    private val userRepository: UserRepository,
    private val deleteSavedRemoteDataUseCase: DeleteSavedRemoteDataUseCase,
    private val observeDataChangedForCloudSync: ObserveDataChangedForCloudSync,
) {

    /**
     * Clear user data remotely and locally.
     * Does not delete the user
     * Does not disconnect the user.
     */
    suspend operator fun invoke(): SoulResult<Unit> = SoulResult.runCatching {
        observeDataChangedForCloudSync.skipUpdate {
            userRepository.clearRemoteUserData()
            deleteSavedRemoteDataUseCase()
        }
    }
}