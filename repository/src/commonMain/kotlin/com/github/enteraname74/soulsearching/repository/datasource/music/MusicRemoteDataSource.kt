package com.github.enteraname74.soulsearching.repository.datasource.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import java.time.LocalDateTime
import java.util.UUID

interface MusicRemoteDataSource {
    suspend fun checkForDeletedSongs(
        musicIds: List<UUID>,
    ): SoulResult<List<UUID>>

    suspend fun fetchSongsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<Music>>
}