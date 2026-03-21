package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.repository.UserRepository
import com.github.enteraname74.soulsearching.repository.datasource.user.UserLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
) : UserRepository {
    override suspend fun signUp(
        username: String,
        password: String
    ): SoulResult<Unit> = SoulResult.runCatching {
        val user: User = remoteDataSource.signUp(
            username = username,
            password = password,
        )
        localDataSource.upsert(user)
    }

    override suspend fun signIn(
        username: String,
        password: String,
        code: String
    ): SoulResult<Unit> = SoulResult.runCatching {
        val user: User = remoteDataSource.signIn(
            username = username,
            password = password,
            code = code,
        )
        localDataSource.upsert(user)
    }

    override fun observeUser(): Flow<User?> =
        localDataSource.observeUser()

    override suspend fun logout() {
        localDataSource.clear()
    }
}