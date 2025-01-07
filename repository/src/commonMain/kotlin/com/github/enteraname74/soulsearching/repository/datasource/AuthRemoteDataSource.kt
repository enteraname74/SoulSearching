package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {
    suspend fun signIn(user: User): SoulResult
    suspend fun logIn(user: User): SoulResult

    fun getHost(): Flow<String>

    suspend fun setHost(host: String)
}