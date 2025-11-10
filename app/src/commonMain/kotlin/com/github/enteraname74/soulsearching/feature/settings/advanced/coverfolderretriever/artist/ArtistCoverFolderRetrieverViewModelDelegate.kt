package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist

import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverViewModelDelegate

class ArtistCoverFolderRetrieverViewModelDelegate(
    private val commonArtistUseCase: CommonArtistUseCase,
    settings: SoulSearchingSettings,
    loadingManager: LoadingManager,
): CoverFolderRetrieverViewModelDelegate(
    settings = settings,
    loadingManager = loadingManager,
) {
    override val settingsKey: String = SoulSearchingSettingsKeys.Cover.ARTIST_COVER_FOLDER_RETRIEVER.key

    override suspend fun handleToggleActivation(isActivated: Boolean) {
        commonArtistUseCase.toggleCoverFolderMode(isActivated)
    }
}