package com.github.enteraname74.domain.usecase.auth

import com.github.enteraname74.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetCloudHostUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<String> =
        authRepository.getHost()
}