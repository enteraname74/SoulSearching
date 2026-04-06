package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.domain.model.UserTokens
import com.github.enteraname74.soulsearching.remote.di.HttpClientNames
import com.github.enteraname74.soulsearching.remote.ext.bodyOrThrow
import com.github.enteraname74.soulsearching.remote.ext.clearToken
import com.github.enteraname74.soulsearching.remote.ext.safeRequest
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.model.GeneratedCode
import com.github.enteraname74.soulsearching.remote.model.RemoteUserAuth
import com.github.enteraname74.soulsearching.remote.model.UserLogin
import com.github.enteraname74.soulsearching.remote.model.UserSignIn
import com.github.enteraname74.soulsearching.remote.resource.AuthResource
import com.github.enteraname74.soulsearching.remote.resource.UserResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.Locale

class UserRemoteDataSourceImpl(
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
    private val client: HttpClient,
) : UserRemoteDataSource, KoinComponent {
    private val cloudClient: HttpClient by inject(qualifier = named(HttpClientNames.CLOUD))

    override suspend fun signIn(
        username: String,
        password: String
    ): User {
        return client
            .withUrl(url = cloudPreferencesDataSource.getUrl())
            .post(AuthResource.LogIn()) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.AcceptLanguage, Locale.getDefault().language)
                setBody(
                    UserLogin(
                        username = username,
                        password = password,
                    )
                )
            }.bodyOrThrow<RemoteUserAuth>().toUser()
    }

    override suspend fun signUp(
        username: String,
        password: String,
        code: String
    ): User =
        client
            .withUrl(url = cloudPreferencesDataSource.getUrl())
            .post(AuthResource.SignIn()) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.AcceptLanguage, Locale.getDefault().language)
                setBody(
                    UserSignIn(
                        username = username,
                        password = password,
                        inscriptionCode = code,
                    )
                )
            }.bodyOrThrow<RemoteUserAuth>().toUser()

    override suspend fun refreshTokens(): SoulResult<UserTokens> =
        cloudClient
            .withUrl(url = cloudPreferencesDataSource.getUrl())
            .safeRequest {
                get(AuthResource.RefreshTokens())
            }

    override suspend fun generateCode(): SoulResult<String> =
        cloudClient
            .withUrl(url = cloudPreferencesDataSource.getUrl())
            .safeRequest<GeneratedCode> {
                get(UserResource.GenerateCode())
            }.map { it.code }

    override suspend fun logout() {
        cloudClient.clearToken()
    }
}