package com.github.enteraname74.soulsearching.features.musicmanager.persistence

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.features.musicmanager.domain.OptimizedCachedData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Saves the collected data of music, album and artists.
 */
class MusicPersistence(
    private val optimizedCachedData: OptimizedCachedData,
): KoinComponent {
    private val commonMusicsUseCase: CommonMusicUseCase by inject()
    private val commonFolderUseCase: CommonFolderUseCase by inject()
    private val settings: SoulSearchingSettings by inject()

    suspend fun saveAll() {
        commonMusicsUseCase.upsertAll(optimizedCachedData.musicsByPath.values.toList())
        commonFolderUseCase.upsertAll(
            optimizedCachedData.musicsByPath.values.map { Folder(folderPath = it.folder) }.distinctBy { it.folderPath }
        )

        optimizedCachedData.clear()

        settings.set(
            SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY.key,
            true
        )
    }
}