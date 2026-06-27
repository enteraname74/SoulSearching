package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.user.UserInscriptionCode
import com.github.enteraname74.soulsearching.remote.ext.bodyOrThrow
import com.github.enteraname74.soulsearching.remote.ext.successOrThrow
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.resource.UserResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.code.UserInscriptionCodeRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import kotlin.uuid.Uuid

class UserInscriptionCodeRemoteDataSourceImpl(
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
    private val client: HttpClient,
) : UserInscriptionCodeRemoteDataSource {
    override suspend fun generate(): UserInscriptionCode =
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .get(UserResource.GenerateCode())
            .bodyOrThrow()

    override suspend fun fetchAll(): List<UserInscriptionCode> =
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .get(UserResource.AllCodes())
            .bodyOrThrow()

    override suspend fun delete(code: Uuid) {
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .delete(UserResource.Code(id = code))
            .successOrThrow()
    }
}