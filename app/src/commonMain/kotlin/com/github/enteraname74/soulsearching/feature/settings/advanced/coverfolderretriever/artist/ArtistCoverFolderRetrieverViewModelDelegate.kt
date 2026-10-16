package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist

import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverViewModelDelegate

class ArtistCoverFolderRetrieverViewModelDelegate(
    private val commonArtistUseCase: CommonArtistUseCase,
    settingsRepository: SettingsRepository,
    loadingManager: LoadingManager,
    workDispatcher: WorkDispatcher,
): CoverFolderRetrieverViewModelDelegate(
    settingsRepository = settingsRepository,
    loadingManager = loadingManager,
    workDispatcher = workDispatcher,
) {
    override suspend fun handleToggleActivation(isActivated: Boolean) {
        commonArtistUseCase.toggleCoverFolderMode(isActivated)
    }
}
