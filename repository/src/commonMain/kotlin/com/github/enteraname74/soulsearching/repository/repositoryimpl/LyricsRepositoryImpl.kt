package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.LyricsRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class LyricsRepositoryImpl: LyricsRepository {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun getLyricsOfSong(music: Music): String? {
        val initialPath = "https://lyrist.vercel.app/api/${music.name}/${music.artist}"
        val encodedPath = initialPath.replace(" ", "%20")

        return try {
            val response = client.get(encodedPath)

            if (response.status.isSuccess()) {
                response.body<LyricsResult>().lyrics
            } else {
                null
            }
        } catch (e: Exception) {
            println("LYRICS PROVIDER: ERROR : $e")
            null
        }
    }
}

@Serializable
private data class LyricsResult(
    val lyrics: String
)