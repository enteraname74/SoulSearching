package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys

class SaveInitialFetchedMusicsUseCase(
    private val commonMusicsUseCase: CommonMusicUseCase,
    private val settings: SoulSearchingSettings,
) {
    suspend operator fun invoke(musics: List<Music>) {
        commonMusicsUseCase.upsertAll(musics)
        settings.set(
            SoulSearchingSettingsKeys.HAS_MUSICS_BEEN_FETCHED_KEY.key,
            true
        )
    }
}