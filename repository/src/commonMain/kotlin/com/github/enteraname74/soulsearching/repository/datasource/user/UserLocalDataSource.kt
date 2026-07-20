package com.github.enteraname74.soulsearching.repository.datasource.user

import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

interface UserLocalDataSource {
    suspend fun upsert(user: User)
    suspend fun clear()
    fun observeUser(): Flow<User?>

    suspend fun setUsers(users: List<SimpleUser>)

    fun observeAll(): Flow<List<SimpleUser>>

    suspend fun deleteAllSimpleUsers()

    suspend fun delete(userId: Uuid)
}