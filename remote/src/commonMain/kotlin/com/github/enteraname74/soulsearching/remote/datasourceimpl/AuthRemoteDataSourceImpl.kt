package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.CloudInscriptionCode
import com.github.enteraname74.domain.model.ConnectedUser
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.model.*
import com.github.enteraname74.soulsearching.remote.model.user.*
import com.github.enteraname74.soulsearching.repository.datasource.auth.AuthRemoteDataSource
import com.github.enteraname74.soulsearching.repository.model.UserAuth
import com.github.enteraname74.soulsearching.repository.model.UserTokens
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class AuthRemoteDataSourceImpl(
    private val client: HttpClient,
    private val settings: SoulSearchingSettings,
) : AuthRemoteDataSource, KoinComponent {
    private val cloudClient by inject<HttpClient>(
        qualifier = named(HttpClientNames.CLOUD_AUTH)
    )

    override suspend fun signIn(user: User, inscriptionCode: String): SoulResult<UserAuth> {
        val result = client.safeRequest<RemoteUserAuth> {
            this.post(urlString = ServerRoutes.Auth.SIGN_IN) {
                contentType(ContentType.Application.Json)
                setBody(user.toUserSignIn(inscriptionCode))
            }
        }

        return result.map {
            it.toUserAuth()
        }
    }

    override suspend fun logIn(user: User): SoulResult<UserAuth> {
        val result = client.safeRequest<RemoteUserAuth> {
            this.post(urlString = ServerRoutes.Auth.LOG_IN) {
                contentType(ContentType.Application.Json)
                setBody(user.toUserLogIn())
            }
        }

        return result.map {
            it.toUserAuth()
        }
    }

    override suspend fun generateInscriptionCode(): SoulResult<CloudInscriptionCode> =
        cloudClient.safeRequest<RemoteInscriptionCode> {
            this.get(urlString = ServerRoutes.User.GENERATE_CODE)
        }.map { it.toInscriptionCode() }

    override suspend fun refreshTokens(): SoulResult<UserTokens> {
        val result = cloudClient.safeRequest<RemoteUserTokens> {
            this.get(urlString = ServerRoutes.Auth.REFRESH_TOKENS)
        }

        return result.map {
            it.toUserTokens()
        }
    }

    override suspend fun logOut() {
        cloudClient.clearToken()
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