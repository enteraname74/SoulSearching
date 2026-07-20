package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.repository.UserRepository
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
) : UserRepository {
    override suspend fun signIn(
        username: String,
        password: String
    ): SoulResult<Unit> = SoulResult.runCatching {
        val user: User = remoteDataSource.signIn(
            username = username,
            password = password,
        )
        localDataSource.upsert(user)
    }

    override suspend fun signUp(
        username: String,
        password: String,
        code: String
    ): SoulResult<Unit> = SoulResult.runCatching {
        val user: User = remoteDataSource.signUp(
            username = username,
            password = password,
            code = code,
        )
        localDataSource.upsert(user)
    }

    override fun observeUser(): Flow<User?> =
        localDataSource.observeUser()

    override suspend fun logout() {
        remoteDataSource.logout()
        localDataSource.clear()
        localDataSource.deleteAllSimpleUsers()
    }

    override suspend fun fetchAll() : SoulResult<Unit> = SoulResult.runCatching {
        val users: List<SimpleUser> = remoteDataSource.fetchAll()
        localDataSource.setUsers(users)
    }

    override fun observeAll(): Flow<List<SimpleUser>> =
        localDataSource.observeAll()

    override suspend fun delete(userId: Uuid): SoulResult<Unit> = SoulResult.runCatching {
        remoteDataSource.delete(userId = userId)
        localDataSource.delete(userId = userId)
    }
}