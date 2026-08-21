package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.model.user.UserStorage
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface UserRepository {
    suspend fun signIn(
        username: String,
        password: String,
    ): SoulResult<Unit>

    suspend fun signUp(
        username: String,
        password: String,
        code: String,
    ): SoulResult<Unit>

    fun observeUser(): Flow<User?>

    suspend fun logout()

    suspend fun clearRemoteUserData()

    suspend fun fetchAll(): SoulResult<Unit>

    fun observeAll(): Flow<List<SimpleUser>>

    suspend fun delete(userId: Uuid): SoulResult<Unit>

    suspend fun deleteCurrentUser()

    suspend fun fetchUserStorage(): SoulResult<Unit>

    fun observeUserStorage(): Flow<UserStorage?>
}
