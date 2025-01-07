package com.github.enteraname74.domain.usecase.auth

import com.github.enteraname74.domain.repository.AuthRepository

class SetCloudHostUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(host: String) {
        authRepository.setHost(host)
    }
}