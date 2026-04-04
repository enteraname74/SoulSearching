package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.player.SharedPlayedList
import com.github.enteraname74.domain.model.player.SharedPlayerMusic
import com.github.enteraname74.soulsearching.remote.ext.bodyOrError
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.model.player.CheckPlayerMusicIdsBody
import com.github.enteraname74.soulsearching.remote.model.player.NewPlayedListBody
import com.github.enteraname74.soulsearching.remote.resource.PlayerResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.player.PlayerRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.uuid.Uuid

class PlayerRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
) : PlayerRemoteDataSource {
    override suspend fun create(
        deviceId: String,
        musicIds: List<String>
    ): SharedPlayedList {
        val body = NewPlayedListBody(
            deviceId = deviceId,
            musicIds = musicIds,
        )
        println("CLUELESS -- body: $body")

        return client.withUrl(cloudPreferencesDataSource.getUrl())
            .post(PlayerResource()) {
                contentType(ContentType.Application.Json)
                setBody(
                    NewPlayedListBody(
                        deviceId = deviceId,
                        musicIds = musicIds,
                    )
                )
            }.bodyOrError()
    }

    override suspend fun getDeletedMusicIds(
        deviceId: String,
        listId: Uuid,
        musicIdsToCheck: List<String>
    ): List<String> =
        client.withUrl(cloudPreferencesDataSource.getUrl())
            .post(PlayerResource.Check()) {
                contentType(ContentType.Application.Json)
                setBody(
                    CheckPlayerMusicIdsBody(
                        musicIds = musicIdsToCheck,
                        deviceId = deviceId,
                        listId = listId,
                    )
                )
            }.bodyOrError()

    override suspend fun getPlayedList(
        deviceId: String,
        listId: Uuid
    ): SharedPlayedList =
        client.withUrl(cloudPreferencesDataSource.getUrl())
            .get(
                resource = PlayerResource.List(
                    listId = listId,
                    deviceId = deviceId,
                )
            ).bodyOrError()

    override suspend fun getPlayedListMusics(
        deviceId: String,
        listId: Uuid,
        lastUpdateAt: Long?,
        maxPerPage: Int?,
        page: Int?
    ): List<SharedPlayerMusic> =
        client.withUrl(cloudPreferencesDataSource.getUrl())
            .get(
                resource = PlayerResource.GetMusics(
                    listId = listId,
                    deviceId = deviceId,
                    lastUpdateAt = lastUpdateAt,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            ).bodyOrError()
}