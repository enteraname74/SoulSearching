package com.github.enteraname74.domain.usecase.auth

import com.github.enteraname74.domain.repository.AuthRepository

class LogOutUserUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() {
        authRepository.logOut()
    }
}