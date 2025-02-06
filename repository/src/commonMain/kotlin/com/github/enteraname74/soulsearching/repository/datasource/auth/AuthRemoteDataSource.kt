package com.github.enteraname74.soulsearching.repository.datasource.auth

import com.github.enteraname74.domain.model.CloudInscriptionCode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.soulsearching.repository.model.UserTokens
import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {
    suspend fun signIn(user: User, inscriptionCode: String): SoulResult<UserTokens>
    suspend fun logIn(user: User): SoulResult<UserTokens>
    suspend fun generateInscriptionCode(): SoulResult<CloudInscriptionCode>
    suspend fun refreshTokens(): SoulResult<UserTokens>

    /**
     * Log out the user. It will remove the tokens of the cloud client.
     */
    suspend fun logOut()

    fun getHost(): Flow<String>

    suspend fun setHost(host: String)
}