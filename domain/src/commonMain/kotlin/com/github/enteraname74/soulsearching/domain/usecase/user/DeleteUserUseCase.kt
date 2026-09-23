package com.github.enteraname74.soulsearching.domain.usecase.user

import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.repository.UserRepository

class DeleteUserUseCase(
    private val userRepository: UserRepository,
    private val deleteSavedRemoteDataUseCase: DeleteSavedRemoteDataUseCase,
) {

    /**
     * Deletes the current user account and clear all cloud related elements.
     */
    suspend operator fun invoke(): SoulResult<Unit> = SoulResult.runCatching {
        userRepository.deleteCurrentUser()
        deleteSavedRemoteDataUseCase()
    }
}