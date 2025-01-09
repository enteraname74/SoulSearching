package com.github.enteraname74.soulsearching.repository.datasource.auth

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {
    suspend fun signIn(user: User): SoulResult<String>
    suspend fun logIn(user: User): SoulResult<String>

    /**
     * Log out the user. It will remove the tokens of the cloud client.
     */
    suspend fun logOut()

    fun getHost(): Flow<String>

    suspend fun setHost(host: String)
}