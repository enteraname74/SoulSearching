package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.ext.toUUID
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.remote.cloud.ServerRoutes
import com.github.enteraname74.soulsearching.remote.model.JSON
import com.github.enteraname74.soulsearching.remote.model.RemoteMusic
import com.github.enteraname74.soulsearching.remote.model.RemoteResult
import com.github.enteraname74.soulsearching.remote.model.safeRequest
import com.github.enteraname74.soulsearching.repository.datasource.music.MusicRemoteDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.streams.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.*

class MusicRemoteDataSourceImpl(
    private val client: HttpClient
): MusicRemoteDataSource {
    override suspend fun checkForDeletedSongs(musicIds: List<UUID>): SoulResult<List<UUID>> {
        val result: RemoteResult<List<String>> = client.safeRequest {
            get(urlString = ServerRoutes.Music.CHECK) {
                setBody(
                    musicIds.map { it.toString() }
                )
                contentType(ContentType.Application.Json)
            }
        }

        return result.toSoulResult { list ->
            list.mapNotNull { it.toUUID() }
        }
    }

    override suspend fun fetchSongsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<Music>> {
        val result: RemoteResult<List<RemoteMusic>> = client.safeRequest {
            get(
                urlString = ServerRoutes.Music.all(
                    after = after,
                    maxPerPage = maxPerPage,
                    page = page,
                )
            )
        }

        return result.toSoulResult(
            mapData = { songs ->
                songs.map { it.toMusic() }
            }
        )
    }

    override suspend fun uploadMusicToCloud(
        music: Music,
        searchMetadata: Boolean,
        artists: List<String>,
    ): SoulResult<Music?> {
        val file: File = File(music.path).takeIf { it.exists() } ?: return SoulResult.Error(null)

        val contentType = withContext(Dispatchers.IO) {
            Files.probeContentType(file.toPath())
        } ?: "application/octet-stream"

        val result: RemoteResult<String> = client.safeRequest {
            submitFormWithBinaryData(
                url = ServerRoutes.Music.upload(searchMetadata),
                formData = formData {
                    append(
                        key = "file".quote(),
                        value = InputProvider(file.length()) {
                            file.inputStream().asInput()
                        },
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=${file.name.quote()}")
                            append(HttpHeaders.ContentType, contentType)
                        }
                    )
                    append(
                        "metadata",
                        JSON.encodeToString(
                            CustomMusicMetadata
                                .fromMusic(
                                    music = music,
                                    artists = artists,
                                ),
                        ),
                        Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        }
                    )
                }
            )
        }

        return result.toSoulResult {
            try {
                JSON.decodeFromString<RemoteMusic>(it).toMusic()
            } catch (_: Exception) {
                null
            }
        }
    }
}

@Serializable
data class CustomMusicMetadata(
    val name: String?,
    val album: String?,
    val artists: List<String>,
    val duration: Long,
) {
    companion object {
        fun fromMusic(
            music: Music,
            artists: List<String>,
        ): CustomMusicMetadata =
            CustomMusicMetadata(
                name = music.name,
                album = music.album,
                artists = artists,
                duration = music.duration,
            )
    }
}