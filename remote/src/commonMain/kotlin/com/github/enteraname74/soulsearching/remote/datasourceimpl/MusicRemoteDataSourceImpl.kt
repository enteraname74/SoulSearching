package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.ext.toUUID
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.ext.appendFile
import com.github.enteraname74.soulsearching.remote.ext.appendJson
import com.github.enteraname74.soulsearching.remote.ext.contentType
import com.github.enteraname74.soulsearching.remote.model.music.CustomMusicMetadata
import com.github.enteraname74.soulsearching.remote.model.music.RemoteMusic
import com.github.enteraname74.soulsearching.remote.model.music.RemoteUploadedMusicData
import com.github.enteraname74.soulsearching.remote.model.music.toModifiedMusic
import com.github.enteraname74.soulsearching.remote.model.safeRequest
import com.github.enteraname74.soulsearching.remote.model.safeSimpleRequest
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import com.github.enteraname74.soulsearching.repository.model.UploadedMusicResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.io.File
import java.time.LocalDateTime
import java.util.*

class MusicRemoteDataSourceImpl(
    private val client: HttpClient
) : MusicRemoteDataSource {
    override suspend fun checkForDeletedSongs(musicIds: List<UUID>): SoulResult<List<UUID>> {
        val result: SoulResult<List<String>> = client.safeRequest {
            get(urlString = ServerRoutes.Music.CHECK) {
                setBody(
                    musicIds.map { it.toString() }
                )
                contentType(ContentType.Application.Json)
            }
        }

        return result.map { list ->
            list.mapNotNull { it.toUUID() }
        }
    }

    override suspend fun fetchSongsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<Music>> {
        val result: SoulResult<List<RemoteMusic>> = client.safeRequest {
            get(
                urlString = ServerRoutes.Music.all(
                    after = after,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            )
        }

        return result.map { songs ->
            songs.map { it.toMusic() }
        }
    }

    override suspend fun deleteAll(musicIds: List<UUID>): SoulResult<Unit> =
        client.safeRequest<String> {
            delete(urlString = ServerRoutes.Music.DELETE) {
                setBody(musicIds.map { it.toString() })
                contentType(ContentType.Application.Json)
            }
        }.toSimpleResult()

    override suspend fun update(
        music: Music,
        newCover: File?,
        artists: List<String>,
    ): SoulResult<Unit> {
        val contentType: String = newCover?.contentType() ?: ""

        return client.safeSimpleRequest {
            submitFormWithBinaryData(
                url = ServerRoutes.Music.UPDATE,
                formData = formData {
                    newCover?.let {
                        appendFile(
                            key = "cover",
                            contentType = contentType,
                            file = it,
                        )
                    }
                    appendJson(
                        key = "metadata",
                        value = music.toModifiedMusic(artists),
                    )
                }
            ) {
                this.method = HttpMethod.Put
            }
        }.toSimpleResult()
    }

    override suspend fun uploadMusicToCloud(
        music: Music,
        searchMetadata: Boolean,
        artists: List<String>,
    ): SoulResult<UploadedMusicResult> {
        val file: File = File(music.path).takeIf { it.exists() } ?: return SoulResult.Error(null)
        val contentType = file.contentType()

        val result: SoulResult<RemoteUploadedMusicData> = client.safeRequest {
            submitFormWithBinaryData(
                url = ServerRoutes.Music.upload(searchMetadata),
                formData = formData {
                    appendFile(
                        key = "file",
                        file = file,
                        contentType = contentType,
                    )
                    appendJson(
                        key = "metadata",
                        value = CustomMusicMetadata
                            .fromMusic(
                                music = music,
                                artists = artists,
                            ),
                    )
                }
            )
        }

        return result.map { it.toUploadMusicData() }
    }
}