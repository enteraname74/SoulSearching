package com.github.enteraname74.soulsearching.domain.usecase.music

import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository

class SaveInitialFetchedMusicsUseCase(
    private val commonMusicsUseCase: CommonMusicUseCase,
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(musics: List<Music>) {
        commonMusicsUseCase.upsertAll(musics)
        settingsRepository.setHasMusicsBeenFetched(true)
    }
}
