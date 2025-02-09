package com.github.enteraname74.soulsearching.remote.datasourceimpl

import com.github.enteraname74.domain.model.Release
import com.github.enteraname74.soulsearching.remote.model.RemoteRelease
import com.github.enteraname74.soulsearching.repository.datasource.ReleaseDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ReleaseDataSourceImpl(
    private val client: HttpClient,
): ReleaseDataSource {

    override suspend fun getLatestRelease(): Release? {
        val release: RemoteRelease? = try {
            val response = client.get(urlString = LATEST_RELEASE_URL)
            println("GOT RESPONSE ON GET LATEST RELEASE: $response")

            if (response.status.isSuccess()) {
                response.body<RemoteRelease>()
            } else {
                null
            }
        } catch (e: Exception) {
            println("GOT EXCEPTION ON GET LATEST RELEASE: $e")
            null
        }

        return release?.toRelease()
    }

    companion object {
        private const val LATEST_RELEASE_URL = "https://api.github.com/repos/enteraname74/SoulSearching/releases/latest"
    }
}