package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(user: User): SoulResult<Unit>
    suspend fun logIn(user: User): SoulResult<Unit>

    fun getUser(): Flow<User?>

    fun getHost(): Flow<String>

    suspend fun setHost(host: String)
}