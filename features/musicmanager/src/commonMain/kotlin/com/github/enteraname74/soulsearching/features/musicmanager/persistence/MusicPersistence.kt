package com.github.enteraname74.soulsearching.features.musicmanager.persistence

import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Saves the collected data of music, album and artists.
 */
class MusicPersistence: KoinComponent {
    private val commonMusicsUseCase: CommonMusicUseCase by inject()
    private val commonFolderUseCase: CommonFolderUseCase by inject()
    private val settings: SoulSearchingSettings by inject()

    suspend fun saveAll(musics: List<Music>) {
        commonMusicsUseCase.upsertAll(musics)
        commonFolderUseCase.upsertAll(
            musics.map { Folder(folderPath = it.folder) }.distinctBy { it.folderPath }
        )

        settings.set(
            SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY.key,
            true
        )
    }
}