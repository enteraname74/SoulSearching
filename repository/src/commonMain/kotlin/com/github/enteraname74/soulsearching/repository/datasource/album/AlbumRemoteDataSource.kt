package com.github.enteraname74.soulsearching.repository.datasource.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.SoulResult
import java.time.LocalDateTime
import java.util.*

interface AlbumRemoteDataSource {
    suspend fun checkForDeletedAlbums(
        albumIds: List<UUID>,
    ): SoulResult<List<UUID>>

    suspend fun fetchAlbumsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<Album>>

    suspend fun deleteAll(
        albumIds: List<UUID>
    ): SoulResult<Unit>

    suspend fun update(
        album: Album,
        artist: String,
    ): SoulResult<Unit>
}