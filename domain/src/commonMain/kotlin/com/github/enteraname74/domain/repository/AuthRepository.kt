package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.CloudInscriptionCode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(user: User, inscriptionCode: String): SoulResult<Unit>
    suspend fun logIn(user: User): SoulResult<Unit>
    suspend fun generateInscriptionCode(): SoulResult<CloudInscriptionCode>
    suspend fun logOut()

    fun getUser(): Flow<User?>

    fun getHost(): Flow<String>

    suspend fun setHost(host: String)
}