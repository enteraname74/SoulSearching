package com.github.enteraname74.soulsearching.repository.datasource.user

import com.github.enteraname74.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    suspend fun upsert(user: User)
    suspend fun clear()
    fun observeUser(): Flow<User?>
}