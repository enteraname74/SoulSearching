package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.remote.ext.bodyOrThrow
import com.github.enteraname74.soulsearching.remote.ext.safeRequest
import com.github.enteraname74.soulsearching.remote.ext.safeUnitRequest
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.model.MusicIdsBody
import com.github.enteraname74.soulsearching.remote.model.update.MusicUpdate
import com.github.enteraname74.soulsearching.remote.model.update.toMusicUpdate
import com.github.enteraname74.soulsearching.remote.resource.MusicResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*

class MusicRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
    private val workDispatcher: WorkDispatcher,
) : MusicRemoteDataSource {
    override suspend fun getDeletedRemoteMusicIds(idsToCheck: List<String>): List<String> =
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .post(MusicResource.Check()) {
                contentType(ContentType.Application.Json)
                setBody(MusicIdsBody(idsToCheck))
            }.body()

    override suspend fun updateMusicToCloud(music: Music): SoulResult<CloudMusic> {
        val musicUpdate: MusicUpdate = music.toMusicUpdate() ?: return SoulResult.Error()

        return client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .safeRequest {
                put(MusicResource()) {
                    contentType(ContentType.Application.Json)
                    setBody(musicUpdate)
                }.body()
            }
    }

    override suspend fun uploadMusicToCloud(music: Music): SoulResult<CloudMusic> {
        return uploadMusicFile(
            client = client,
            baseUrl = cloudPreferencesDataSource.getUrl(),
            music = music,
            workDispatcher = workDispatcher,
        )
    }

    override suspend fun delete(remoteIds: List<String>): SoulResult<Unit> =
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .safeUnitRequest {
                delete(MusicResource()) {
                    contentType(ContentType.Application.Json)
                    setBody(remoteIds)
                }
            }

    override suspend fun getOfUser(
        lastUpdateAt: Long?,
        maxPerPage: Int?,
        page: Int?
    ): List<CloudMusic> =
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .get(
                resource = MusicResource.OfUser(
                    lastUpdateAt = lastUpdateAt,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            ).bodyOrThrow()
}
