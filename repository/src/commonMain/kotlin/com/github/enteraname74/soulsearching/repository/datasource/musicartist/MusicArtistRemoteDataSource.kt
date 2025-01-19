package com.github.enteraname74.soulsearching.repository.datasource.musicartist

import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.model.SoulResult
import java.time.LocalDateTime

interface MusicArtistRemoteDataSource {
    suspend fun fetchMusicArtistsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<MusicArtist>>
}