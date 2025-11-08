package com.github.enteraname74.soulsearching.features.musicmanager.domain

import com.github.enteraname74.domain.model.*
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Holds optimized data access to fetched elements for quick access.
 */
data class OptimizedCachedData(
    var musicsByPath: HashMap<String, Music> = hashMapOf(),
) {
    fun clear() {
        musicsByPath.clear()
    }

    companion object : KoinComponent {
        private val commonMusicUseCase: CommonMusicUseCase by inject()

        suspend fun fromDb(): OptimizedCachedData = OptimizedCachedData(
            musicsByPath = commonMusicUseCase.getAll().first().associateBy { it.path } as HashMap<String, Music>,
        )
    }
}


