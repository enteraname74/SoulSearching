package com.github.enteraname74.soulsearching.repository.datasource.user

import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.model.user.UserStorage
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface UserLocalDataSource {
    suspend fun upsert(user: User)

    suspend fun clearData()
    fun observeUser(): Flow<User?>

    suspend fun setUsers(users: List<SimpleUser>)

    fun observeAll(): Flow<List<SimpleUser>>

    suspend fun delete(userId: Uuid)

    suspend fun saveUserStorage(userStorage: UserStorage)

    fun observeUserStorage(): Flow<UserStorage?>
}
