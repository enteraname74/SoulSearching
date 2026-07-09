package com.github.enteraname74.soulsearching.repository.datasource.playlist

import com.github.enteraname74.domain.model.CloudPlaylist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SoulResult
import kotlin.uuid.Uuid

interface PlaylistRemoteDataSource {
    suspend fun getOfUser(
        lastUpdateAt: Long?,
        maxPerPage: Int?,
        page: Int?,
    ): List<CloudPlaylist>

    suspend fun deleteAll(ids: List<Uuid>): SoulResult<Unit>

    suspend fun upload(
        playlist: PlaylistWithMusics,
        coverPath: String?,
    ): CloudPlaylist
}