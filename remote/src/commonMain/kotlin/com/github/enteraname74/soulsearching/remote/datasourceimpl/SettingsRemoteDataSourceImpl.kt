package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.soulsearching.domain.model.settings.CloudSoulSearchingSettings
import com.github.enteraname74.soulsearching.remote.ext.bodyOrThrow
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.resource.SettingsResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.SettingsRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class SettingsRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
) : SettingsRemoteDataSource {
    override suspend fun get(lastUpdateAt: Long?): CloudSoulSearchingSettings? {
        val response = client.withUrl(cloudPreferencesDataSource.getUrl()).get(SettingsResource(lastUpdateAt))
        return if (response.status == HttpStatusCode.NoContent) null else response.bodyOrThrow()
    }

    override suspend fun upsert(settings: CloudSoulSearchingSettings): CloudSoulSearchingSettings =
        client.withUrl(cloudPreferencesDataSource.getUrl()).post(SettingsResource()) {
            contentType(ContentType.Application.Json)
            setBody(settings)
        }.bodyOrThrow()
}
