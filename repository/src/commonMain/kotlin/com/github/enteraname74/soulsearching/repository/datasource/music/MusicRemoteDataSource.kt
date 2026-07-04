package com.github.enteraname74.soulsearching.repository.datasource.music

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult

interface MusicRemoteDataSource {
    suspend fun getDeletedRemoteMusicIds(idsToCheck: List<String>): List<String>
    suspend fun updateMusicToCloud(music: Music): SoulResult<CloudMusic>
    suspend fun uploadMusicToCloud(music: Music): SoulResult<CloudMusic>

    suspend fun delete(remoteIds: List<String>): SoulResult<Unit>
    suspend fun getOfUser(
        lastUpdateAt: Long?,
        maxPerPage: Int?,
        page: Int?,
    ): List<CloudMusic>

    suspend fun fetch(url: String): CloudMusic
}
