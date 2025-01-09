package com.github.enteraname74.domain.usecase.auth

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.repository.AuthRepository
import com.github.enteraname74.domain.repository.CloudRepository
import com.github.enteraname74.domain.repository.MusicRepository

class LogOutUserUseCase(
    private val authRepository: AuthRepository,
    private val musicRepository: MusicRepository,
    private val cloudRepository: CloudRepository,
) {
    suspend operator fun invoke() {
        authRepository.logOut()
        musicRepository.deleteAll(DataMode.Cloud)
        cloudRepository.clearLastUpdateDate()
    }
}