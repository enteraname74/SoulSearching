package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.CloudInscriptionCode
import com.github.enteraname74.domain.model.ConnectedUser
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.repository.AuthRepository
import com.github.enteraname74.soulsearching.repository.datasource.auth.AuthLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.auth.AuthRemoteDataSource
import com.github.enteraname74.soulsearching.repository.model.UserAuth
import com.github.enteraname74.soulsearching.repository.model.UserTokens
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
): AuthRepository {
    override suspend fun signIn(user: User, inscriptionCode: String): SoulResult<Unit> {
        val result: SoulResult<UserAuth> = authRemoteDataSource.signIn(user, inscriptionCode)

        (result as? SoulResult.Success)?.data?.let { userAuth ->
            authLocalDataSource.setUser(userAuth.user)
            authLocalDataSource.setUserTokens(userAuth.tokens)
        }

        return result.toSimpleResult()
    }


    override suspend fun logIn(user: User): SoulResult<Unit> {
        val result: SoulResult<UserAuth> = authRemoteDataSource.logIn(user)

        (result as? SoulResult.Success)?.data?.let { userAuth ->
            authLocalDataSource.setUser(userAuth.user)
            authLocalDataSource.setUserTokens(userAuth.tokens)
        }

        return result.toSimpleResult()
    }

    override suspend fun generateInscriptionCode(): SoulResult<CloudInscriptionCode> =
        authRemoteDataSource.generateInscriptionCode()

    override suspend fun logOut() {
        authLocalDataSource.setUserTokens(
            UserTokens(
                accessToken = "",
                refreshToken = "",
            )
        )
        authLocalDataSource.setUser(null)
        authRemoteDataSource.logOut()
    }

    override fun getUser(): Flow<ConnectedUser?> =
        authLocalDataSource.getUser()

    override fun getHost(): Flow<String> =
        authRemoteDataSource.getHost()

    override suspend fun setHost(host: String) {
        authRemoteDataSource.setHost(host)
    }
}