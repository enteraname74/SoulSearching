package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.cover.CoverRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.flow.firstOrNull

class CoverRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
): CoverRemoteDataSource {
    override suspend fun getRemoteCover(cover: Cover.Url): ByteArray? =
        runCatching {
            client
                .withUrl(cloudPreferencesDataSource.observeUrl().firstOrNull().orEmpty())
                .get(urlString = cover.url)
                .bodyAsBytes()
        }.getOrNull()
}