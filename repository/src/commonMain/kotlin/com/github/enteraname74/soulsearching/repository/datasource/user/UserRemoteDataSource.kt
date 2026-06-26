package com.github.enteraname74.soulsearching.repository.datasource.user

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.model.UserTokens

interface UserRemoteDataSource {
    suspend fun signIn(
        username: String,
        password: String,
    ): User

    suspend fun signUp(
        username: String,
        password: String,
        code: String,
    ): User

    suspend fun refreshTokens(): SoulResult<UserTokens>

    suspend fun logout()
}