package com.github.enteraname74.domain.util

import com.github.enteraname74.domain.model.Music
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Lyrics provider for musics.
 */
class LyricsProvider {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Retrieves the lyrics of a song.
     * If no lyrics are found, returns null.
     */
    suspend fun getLyricsOfSong(music: Music): String? {
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

    @Serializable
    private data class LyricsResult(
        val lyrics: String
    )
}