package com.github.enteraname74.domain.usecase.user

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class CommonUserUseCase(
    private val userRepository: UserRepository,
) {
    suspend fun signIn(
        username: String,
        password: String,
    ): SoulResult<Unit> =
        userRepository.signIn(
            username = username,
            password = password,
        )

    suspend fun signUp(
        username: String,
        password: String,
        code: String,
    ): SoulResult<Unit> =
        userRepository.signUp(
            username = username,
            password = password,
            code = code,
        )

    fun observeUser(): Flow<User?> =
        userRepository.observeUser()

    fun observeAll(): Flow<List<SimpleUser>> =
        userRepository.observeAll()

    suspend fun fetchAll(): SoulResult<Unit> =
        userRepository.fetchAll()

    suspend fun delete(userId: Uuid): SoulResult<Unit> =
        userRepository.delete(userId)
}