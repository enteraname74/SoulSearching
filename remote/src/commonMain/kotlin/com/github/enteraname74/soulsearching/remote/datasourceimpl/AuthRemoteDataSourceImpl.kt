package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.remote.cloud.CloudLocalDataSource
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.model.*
import com.github.enteraname74.soulsearching.repository.datasource.AuthRemoteDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow

class AuthRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudLocalDataSource: CloudLocalDataSource,
    private val settings: SoulSearchingSettings,
): AuthRemoteDataSource {
    override suspend fun signIn(user: User): SoulResult<Unit> {
        val result = client.safeRequest<RemoteToken> {
            this.post(urlString = ServerRoutes.Auth.SIGN_IN) {
                contentType(ContentType.Application.Json)
                setBody(user.toRemoteUser())
            }
        }

        (result as? RemoteResult.Success<RemoteToken>)?.result?.token?.let { token ->
            cloudLocalDataSource.setUser(user)
            cloudLocalDataSource.setToken(token)
        }

        return result.toSimpleResult()
    }

    override suspend fun logIn(user: User): SoulResult<Unit> {
        val result = client.safeRequest<RemoteToken> {
            this.post(urlString = ServerRoutes.Auth.LOG_IN) {
                contentType(ContentType.Application.Json)
                setBody(user.toRemoteUser())
            }
        }

        (result as? RemoteResult.Success<RemoteToken>)?.result?.token?.let { token ->
            cloudLocalDataSource.setUser(user)
            cloudLocalDataSource.setToken(token)
        }

        return result.toSimpleResult()
    }

    override fun getHost(): Flow<String> =
        settings.getFlowOn(SoulSearchingSettingsKeys.Cloud.HOST)

    override suspend fun setHost(host: String) {
        settings.set(
            SoulSearchingSettingsKeys.Cloud.HOST.key,
            host,
        )
    }
}