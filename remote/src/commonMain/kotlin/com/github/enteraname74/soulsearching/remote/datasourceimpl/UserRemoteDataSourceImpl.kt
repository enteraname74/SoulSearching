package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.User
import com.github.enteraname74.soulsearching.remote.ext.bodyOrError
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.model.RemoteUserAuth
import com.github.enteraname74.soulsearching.remote.model.UserLogin
import com.github.enteraname74.soulsearching.remote.model.UserSignIn
import com.github.enteraname74.soulsearching.remote.resource.AuthResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.user.UserRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.flow.firstOrNull
import java.util.Locale

class UserRemoteDataSourceImpl(
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
    private val client: HttpClient,
) : UserRemoteDataSource {

    override suspend fun signIn(
        username: String,
        password: String
    ): User {
        return client
            .withUrl(url = cloudPreferencesDataSource.observeUrl().firstOrNull().orEmpty())
            .post(AuthResource.LogIn()) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.AcceptLanguage, Locale.getDefault().language)
                setBody(
                    UserLogin(
                        username = username,
                        password = password,
                    )
                )
            }.bodyOrError<RemoteUserAuth>().toUser()
    }

    override suspend fun signUp(
        username: String,
        password: String,
        code: String
    ): User =
        client
            .withUrl(url = cloudPreferencesDataSource.observeUrl().firstOrNull().orEmpty())
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
            }.bodyOrError<RemoteUserAuth>().toUser()
}