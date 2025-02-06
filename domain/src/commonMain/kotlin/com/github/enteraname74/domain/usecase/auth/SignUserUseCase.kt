package com.github.enteraname74.domain.usecase.auth

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.repository.AuthRepository

class SignUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User, inscriptionCode: String): SoulResult<*> =
        authRepository.signIn(user, inscriptionCode)
}