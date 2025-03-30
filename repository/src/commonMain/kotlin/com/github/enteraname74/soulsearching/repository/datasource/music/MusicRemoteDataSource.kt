package com.github.enteraname74.soulsearching.repository.datasource.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.repository.model.UploadedMusicResult
import java.io.File
import java.time.LocalDateTime
import java.util.*

interface MusicRemoteDataSource {
    suspend fun checkForDeletedSongs(
        musicIds: List<UUID>,
    ): SoulResult<List<UUID>>

    suspend fun fetchSongsFromCloud(
        after: LocalDateTime?,
        maxPerPage: Int,
        page: Int,
    ): SoulResult<List<Music>>

    suspend fun uploadMusicToCloud(
        music: Music,
        searchMetadata: Boolean,
        artists: List<String>,
    ): SoulResult<UploadedMusicResult>

    suspend fun deleteAll(
        musicIds: List<UUID>
    ): SoulResult<Unit>

    suspend fun update(
        music: Music,
        newCover: File?,
        artists: List<String>,
    ): SoulResult<Unit>
}