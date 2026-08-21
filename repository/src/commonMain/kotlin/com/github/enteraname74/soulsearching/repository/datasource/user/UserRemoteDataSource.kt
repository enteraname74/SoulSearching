package com.github.enteraname74.soulsearching.repository.datasource.user

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.model.user.UserStorage
import com.github.enteraname74.domain.model.user.UserTokens
import kotlin.uuid.Uuid

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

    suspend fun fetchAll(): List<SimpleUser>

    suspend fun delete(userId: Uuid)

    suspend fun fetchUserStorage(): UserStorage

    suspend fun clearUserData(): UserStorage
}
