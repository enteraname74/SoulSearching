package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.remote.ext.appendCoverFile
import com.github.enteraname74.soulsearching.remote.ext.appendJson
import com.github.enteraname74.soulsearching.remote.ext.bodyOrThrow
import com.github.enteraname74.soulsearching.remote.ext.safeUnitRequest
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.model.MusicIdsBody
import com.github.enteraname74.soulsearching.remote.model.update.MusicUpdate
import com.github.enteraname74.soulsearching.remote.model.update.toMusicUpdate
import com.github.enteraname74.soulsearching.remote.resource.MusicResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
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

class MusicRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
    private val workDispatcher: WorkDispatcher,
    private val coverFileManager: CoverFileManager,
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
        val coverPath: String? = (music.cover as? Cover.CoverFile)?.fileCoverId?.let { coverId ->
            coverFileManager.getCoverPath(coverId)
        }

        return client
            .submitFormWithBinaryData(
                url = "${cloudPreferencesDataSource.getUrl()}/music",
                formData = formData {
                    appendJson(
                        key = "metadata",
                        value = musicUpdate,
                    )
                    coverPath?.let {
                        appendCoverFile(
                            key = "cover",
                            path = it,
                            workDispatcher = workDispatcher,
                        )
                    }
                },
            ) {
                this.method = HttpMethod.Put
            }.bodyOrThrow()
    }

    override suspend fun uploadMusicToCloud(music: Music): SoulResult<CloudMusic> =
        uploadMusicFile(
            client = client,
            baseUrl = cloudPreferencesDataSource.getUrl(),
            music = music,
            coverPath = (music.cover as? Cover.CoverFile)?.fileCoverId?.let { coverId ->
                coverFileManager.getCoverPath(coverId)
            },
            workDispatcher = workDispatcher,
        )

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
