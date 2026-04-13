package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.player.PlayedListState
import com.github.enteraname74.domain.model.player.SharedPlayedList
import com.github.enteraname74.domain.model.player.SharedPlayerMusic
import com.github.enteraname74.domain.repository.SharedPlayedListListener
import com.github.enteraname74.soulsearching.remote.ext.bodyOrThrow
import com.github.enteraname74.soulsearching.remote.ext.successOrThrow
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.model.player.CheckPlayerMusicIdsBody
import com.github.enteraname74.soulsearching.remote.model.player.JoinPlayedListBody
import com.github.enteraname74.soulsearching.remote.model.player.MusicsOperationOnPlayedListBody
import com.github.enteraname74.soulsearching.remote.model.player.NewPlayedListBody
import com.github.enteraname74.soulsearching.remote.model.player.RemoveUserFromPlayedListBody
import com.github.enteraname74.soulsearching.remote.model.player.UpdateCurrentMusicBody
import com.github.enteraname74.soulsearching.remote.model.player.UpdatePlayedListBody
import com.github.enteraname74.soulsearching.remote.resource.PlayerResource
import com.github.enteraname74.soulsearching.remote.utils.PlayerUserCommunication
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.player.PlayerRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.uuid.Uuid

class PlayerRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
    private val playerUserCommunication: PlayerUserCommunication,
) : PlayerRemoteDataSource {
    override suspend fun create(
        deviceId: String,
        musicIds: List<String>
    ): SharedPlayedList =
        client.withUrl(cloudPreferencesDataSource.getUrl())
            .post(PlayerResource()) {
                contentType(ContentType.Application.Json)
                setBody(
                    NewPlayedListBody(
                        deviceId = deviceId,
                        musicIds = musicIds,
                    )
                )
            }.bodyOrThrow()

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
            }.bodyOrThrow()

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
            ).bodyOrThrow()

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
            ).bodyOrThrow()

    override suspend fun deletePlayedList(deviceId: String, listId: Uuid) {
        // Not and issue if the call fails
        runCatching {
            client.withUrl(cloudPreferencesDataSource.getUrl())
                .delete(
                    resource = PlayerResource.List(
                        listId = listId,
                        deviceId = deviceId,
                    ),
                ).successOrThrow()
        }
    }

    override suspend fun removeUserFromPlayedList(
        deviceId: String,
        listId: Uuid,
        userId: Uuid,
        deviceIdToRemove: String,
        userIdToRemove: Uuid,
    ) {
        client.withUrl(cloudPreferencesDataSource.getUrl())
            .delete(PlayerResource.RemoveUser()) {
                contentType(ContentType.Application.Json)
                setBody(
                    RemoveUserFromPlayedListBody(
                        listId = listId,
                        deviceId = deviceId,
                        userIdToRemove = userIdToRemove,
                        deviceIdToRemove = deviceIdToRemove,
                    )
                )
            }
    }

    override suspend fun addMusics(
        deviceId: String,
        listId: Uuid,
        musicIds: List<String>
    ) {
        client.withUrl(cloudPreferencesDataSource.getUrl())
            .post(PlayerResource.Musics()) {
                contentType(ContentType.Application.Json)
                setBody(
                    MusicsOperationOnPlayedListBody(
                        deviceId = deviceId,
                        listId = listId,
                        musicIds = musicIds,
                    )
                )
            }.successOrThrow()
    }

    override suspend fun removeMusics(
        deviceId: String,
        listId: Uuid,
        musicIds: List<String>
    ) {
        client.withUrl(cloudPreferencesDataSource.getUrl())
            .delete(PlayerResource.Musics()) {
                contentType(ContentType.Application.Json)
                setBody(
                    MusicsOperationOnPlayedListBody(
                        deviceId = deviceId,
                        listId = listId,
                        musicIds = musicIds,
                    )
                )
            }.successOrThrow()
    }

    override suspend fun updateState(
        deviceId: String,
        listId: Uuid,
        state: PlayedListState
    ): SharedPlayedList =
        client.withUrl(cloudPreferencesDataSource.getUrl())
            .put(PlayerResource()) {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdatePlayedListBody(
                        listId = listId,
                        deviceId = deviceId,
                        state = SharedPlayedList.State.fromPlayedListState(state),
                    )
                )
            }.bodyOrThrow()

    override suspend fun updateCurrentMusic(
        deviceId: String,
        listId: Uuid,
        musicRemoteId: String
    ) {
        client.withUrl(cloudPreferencesDataSource.getUrl())
            .put(PlayerResource.Musics()) {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateCurrentMusicBody(
                        listId = listId,
                        deviceId = deviceId,
                        musicId = musicRemoteId,
                    )
                )
            }.successOrThrow()
    }

    override suspend fun joinSharedList(deviceId: String, code: String): SharedPlayedList =
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .post(PlayerResource.Join()) {
                contentType(ContentType.Application.Json)
                setBody(
                    JoinPlayedListBody(
                        code = code,
                        deviceId = deviceId,
                    )
                )
            }.bodyOrThrow()

    override suspend fun registerSharedPlayedListEventsListener(
        listId: Uuid,
        userId: Uuid,
        deviceId: String,
        listener: SharedPlayedListListener
    ) {
        playerUserCommunication.register(
            listId = listId,
            userId = userId,
            deviceId = deviceId,
            listener = listener,
        )
    }

    override suspend fun removeSharedPlayedListEventsListener() {
        playerUserCommunication.unregister()
    }
}