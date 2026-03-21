package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun signUp(
        username: String,
        password: String,
    ): SoulResult<Unit>

    suspend fun signIn(
        username: String,
        password: String,
        code: String,
    ): SoulResult<Unit>

    fun observeUser(): Flow<User?>

    suspend fun logout()
}