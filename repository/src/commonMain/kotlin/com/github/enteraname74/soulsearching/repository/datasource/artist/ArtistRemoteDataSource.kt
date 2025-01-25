package com.github.enteraname74.soulsearching.repository.datasource.artist

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.SoulResult
import java.time.LocalDateTime
import java.util.*

interface ArtistRemoteDataSource {
    suspend fun checkForDeletedArtists(
        artistIds: List<UUID>,
    ): SoulResult<List<UUID>>

    suspend fun fetchArtistsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<Artist>>

    suspend fun deleteAll(
        artistIds: List<UUID>
    ): SoulResult<String>
}