package com.github.enteraname74.domain.usecase.auth

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.repository.*
import com.github.enteraname74.domain.usecase.cloud.DeleteCloudDataUseCase

class LogOutUserUseCase(
    private val authRepository: AuthRepository,
    private val deleteCloudDataUseCase: DeleteCloudDataUseCase
) {
    suspend operator fun invoke() {
        authRepository.logOut()
        deleteCloudDataUseCase()
    }
}