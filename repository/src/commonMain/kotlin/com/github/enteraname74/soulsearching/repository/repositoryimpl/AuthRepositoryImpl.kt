package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.repository.AuthRepository
import com.github.enteraname74.soulsearching.repository.datasource.AuthRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val settings: SoulSearchingSettings,
): AuthRepository {
    override suspend fun signIn(user: User): SoulResult =
        authRemoteDataSource.signIn(user)

    override suspend fun logIn(user: User): SoulResult =
        authRemoteDataSource.logIn(user)

    override fun getUser(): Flow<User?> =
        combine(
            settings.getFlowOn(SoulSearchingSettingsKeys.Cloud.USERNAME),
            settings.getFlowOn(SoulSearchingSettingsKeys.Cloud.PASSWORD),
        ) { username, password ->
            User(
                username = username,
                password = password
            ).takeIf { it.isValid() }
        }

    override fun getHost(): Flow<String> =
        authRemoteDataSource.getHost()

    override suspend fun setHost(host: String) {
        authRemoteDataSource.setHost(host)
    }
}