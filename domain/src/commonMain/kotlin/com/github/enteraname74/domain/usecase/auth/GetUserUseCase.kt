package com.github.enteraname74.domain.usecase.auth

import com.github.enteraname74.domain.model.ConnectedUser
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<ConnectedUser?> =
        authRepository.getUser()
}