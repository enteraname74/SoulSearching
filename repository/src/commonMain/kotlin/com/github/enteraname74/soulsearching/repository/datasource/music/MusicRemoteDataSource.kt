package com.github.enteraname74.soulsearching.repository.datasource.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult

interface MusicRemoteDataSource {
    suspend fun fetchAllMusicOfUser(): SoulResult<List<Music>>
}