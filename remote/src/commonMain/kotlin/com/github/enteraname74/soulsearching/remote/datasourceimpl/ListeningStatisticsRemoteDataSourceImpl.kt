package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.statistics.CloudListeningStatistics
import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.domain.model.statistics.toCloud
import com.github.enteraname74.soulsearching.remote.ext.bodyOrThrow
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.resource.StatisticsResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.listeningstatistics.ListeningStatisticsRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.uuid.Uuid

class ListeningStatisticsRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
) : ListeningStatisticsRemoteDataSource {
    override suspend fun upsertAll(
        statistics: List<ListeningStatistics>,
        userId: Uuid,
    ) {
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .post(StatisticsResource()) {
                contentType(ContentType.Application.Json)
                setBody(statistics.map { it.toCloud(userId) })
            }
    }

    override suspend fun getOfUser(
        lastUpdateAt: Long?,
        maxPerPage: Int?,
        page: Int?,
    ): List<CloudListeningStatistics> =
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .get(
                resource = StatisticsResource.OfUser(
                    lastUpdateAt = lastUpdateAt,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            ).bodyOrThrow()
}