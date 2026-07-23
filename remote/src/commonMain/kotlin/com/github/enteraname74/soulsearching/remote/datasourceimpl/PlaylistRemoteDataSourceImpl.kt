package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.CloudPlaylist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.remote.ext.appendCoverFile
import com.github.enteraname74.soulsearching.remote.ext.appendJson
import com.github.enteraname74.soulsearching.remote.ext.bodyOrThrow
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.model.PlaylistIdsBody
import com.github.enteraname74.soulsearching.remote.model.upload.toPlaylistUpload
import com.github.enteraname74.soulsearching.remote.resource.PlaylistResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.playlist.PlaylistRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlin.uuid.Uuid

class PlaylistRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
    private val workDispatcher: WorkDispatcher,
) : PlaylistRemoteDataSource {

    override suspend fun getDeletedRemotePlaylistIds(idsToCheck: List<Uuid>): List<Uuid> =
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .post(PlaylistResource.Check()) {
                contentType(ContentType.Application.Json)
                setBody(PlaylistIdsBody(idsToCheck))
            }.body()

    override suspend fun getOfUser(
        lastUpdateAt: Long?,
        maxPerPage: Int?,
        page: Int?
    ): List<CloudPlaylist> =
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .get(
                resource = PlaylistResource.OfUser(
                    lastUpdateAt = lastUpdateAt,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            ).bodyOrThrow()

    override suspend fun deleteAll(ids: List<Uuid>): SoulResult<Unit> = SoulResult.runCatching {
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .delete(PlaylistResource()) {
                contentType(ContentType.Application.Json)
                setBody(ids.map { it.toString() })
            }
    }

    override suspend fun upload(
        playlist: PlaylistWithMusics,
        coverPath: String?
    ): CloudPlaylist =
        client
            .submitFormWithBinaryData(
                url = "${cloudPreferencesDataSource.getUrl()}/playlist",
                formData = formData {
                    appendJson(
                        key = "metadata",
                        value = playlist.toPlaylistUpload(),
                    )
                    coverPath?.let {
                        appendCoverFile(
                            key = "cover",
                            path = it,
                            workDispatcher = workDispatcher,
                        )
                    }
                }
            ) {
                this.method = HttpMethod.Post
            }.bodyOrThrow()
}