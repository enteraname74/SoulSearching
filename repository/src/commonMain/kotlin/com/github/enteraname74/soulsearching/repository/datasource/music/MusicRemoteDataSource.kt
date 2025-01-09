package com.github.enteraname74.soulsearching.repository.datasource.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import java.time.LocalDateTime

interface MusicRemoteDataSource {
    suspend fun fetchSongsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<Music>>
}