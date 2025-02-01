package com.github.enteraname74.soulsearching.repository.datasource.musicplaylist

import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.SoulResult
import java.time.LocalDateTime

interface MusicPlaylistRemoteDataSource {
    suspend fun fetchMusicPlaylistsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<MusicPlaylist>>
}