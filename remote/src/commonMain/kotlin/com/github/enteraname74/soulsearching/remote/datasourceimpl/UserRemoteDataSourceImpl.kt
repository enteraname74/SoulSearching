package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.model.user.SimpleUser
import com.github.enteraname74.domain.model.user.User
import com.github.enteraname74.domain.model.user.UserTokens
import com.github.enteraname74.domain.util.LocaleUtils
import com.github.enteraname74.soulsearching.remote.di.HttpClientNames
import com.github.enteraname74.soulsearching.remote.ext.bodyOrThrow
import com.github.enteraname74.soulsearching.remote.ext.clearToken
import com.github.enteraname74.soulsearching.remote.ext.safeRequest
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.model.user.RemoteSimpleUser
import com.github.enteraname74.soulsearching.remote.model.user.RemoteUserAuth
import com.github.enteraname74.soulsearching.remote.model.user.UserLogin
import com.github.enteraname74.soulsearching.remote.model.user.UserSignIn
import com.github.enteraname74.soulsearching.remote.resource.AuthResource
import com.github.enteraname74.soulsearching.remote.resource.UserResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.delete
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
import kotlin.uuid.Uuid

class UserRemoteDataSourceImpl(
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
    private val client: HttpClient,
) : UserRemoteDataSource, KoinComponent {
    private val authenticatedClient: HttpClient by inject(qualifier = named(HttpClientNames.CLOUD))

    override suspend fun signIn(
        username: String,
        password: String
    ): User {
        return client
            .withUrl(url = cloudPreferencesDataSource.getUrl())
            .post(AuthResource.LogIn()) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.AcceptLanguage, LocaleUtils.currentLanguage())
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
                header(HttpHeaders.AcceptLanguage, LocaleUtils.currentLanguage())
                setBody(
                    UserSignIn(
                        username = username,
                        password = password,
                        inscriptionCode = code,
                    )
                )
            }.bodyOrThrow<RemoteUserAuth>().toUser()

    override suspend fun refreshTokens(): SoulResult<UserTokens> =
        authenticatedClient
            .withUrl(url = cloudPreferencesDataSource.getUrl())
            .safeRequest {
                get(AuthResource.RefreshTokens())
            }

    override suspend fun logout() {
        authenticatedClient.clearToken()
    }

    override suspend fun fetchAll(): List<SimpleUser> =
        authenticatedClient
            .withUrl(url = cloudPreferencesDataSource.getUrl())
            .get(UserResource())
            .bodyOrThrow<List<RemoteSimpleUser>>()
            .map { it.toSimpleUsers() }

    override suspend fun delete(userId: Uuid) {
        authenticatedClient
            .withUrl(url = cloudPreferencesDataSource.getUrl())
            .delete(UserResource.Delete(id = userId))
    }
}
