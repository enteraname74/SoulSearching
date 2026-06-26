package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.ext.appendFile
import com.github.enteraname74.soulsearching.remote.ext.appendJson
import com.github.enteraname74.soulsearching.remote.ext.contentType
import com.github.enteraname74.soulsearching.remote.ext.safeRequest
import com.github.enteraname74.soulsearching.remote.ext.safeUnitRequest
import com.github.enteraname74.soulsearching.remote.ext.withUrl
import com.github.enteraname74.soulsearching.remote.model.MusicIdsBody
import com.github.enteraname74.soulsearching.remote.model.update.MusicUpdate
import com.github.enteraname74.soulsearching.remote.model.update.toMusicUpdate
import com.github.enteraname74.soulsearching.remote.model.upload.toMusicUpload
import com.github.enteraname74.soulsearching.remote.resource.MusicResource
import com.github.enteraname74.soulsearching.repository.datasource.CloudPreferencesDataSource
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.io.File

class MusicRemoteDataSourceImpl(
    private val client: HttpClient,
    private val cloudPreferencesDataSource: CloudPreferencesDataSource,
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
        val file: File = music.localPath
            ?.let { File(it) }
            ?.takeIf { it.exists() } ?: return SoulResult.Error()
        val contentType = file.contentType()

        return client
            .safeRequest {
                submitFormWithBinaryData(
                    url = "${
                        cloudPreferencesDataSource.getUrl()
                    }/music/upload",
                    formData = formData {
                        appendFile(
                            key = "file",
                            file = file,
                            contentType = contentType,
                        )
                        appendJson(
                            key = "metadata",
                            value = music.toMusicUpload(),
                        )
                    }
                )
            }
    }

    override suspend fun delete(remoteIds: List<String>): SoulResult<Unit> =
        client
            .withUrl(cloudPreferencesDataSource.getUrl())
            .safeUnitRequest {
                delete(MusicResource()) {
                    contentType(ContentType.Application.Json)
                    setBody(MusicIdsBody(remoteIds))
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
            ).body()
}