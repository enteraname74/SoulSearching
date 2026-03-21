package com.github.enteraname74.domain.usecase.user

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class CommonUserUseCase(
    private val userRepository: UserRepository,
) {
    suspend fun signUp(
        username: String,
        password: String,
    ): SoulResult<Unit> =
        userRepository.signUp(
            username = username,
            password = password,
        )

    suspend fun signIn(
        username: String,
        password: String,
        code: String,
    ): SoulResult<Unit> =
        userRepository.signIn(
            username = username,
            password = password,
            code = code,
        )

    fun observeUser(): Flow<User?> =
        userRepository.observeUser()

    suspend fun logout() {
        userRepository.logout()
    }
}