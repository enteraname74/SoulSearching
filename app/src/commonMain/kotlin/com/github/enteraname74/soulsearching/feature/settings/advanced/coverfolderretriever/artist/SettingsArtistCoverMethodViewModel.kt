package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

class SettingsArtistCoverMethodViewModel(
    settingsRepository: SettingsRepository,
    private val artistCoverFolderRetrieverViewModelDelegate: ArtistCoverFolderRetrieverViewModelDelegate,
    workDispatcher: WorkDispatcher,
): ViewModel(), CoverFolderRetrieverActions by artistCoverFolderRetrieverViewModelDelegate {
    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<CoverFolderRetrieverState> = settingsRepository.observeSettings().mapLatest { settings ->
        val coverFolderRetriever = settings.cover.artistCoverFolderRetriever
        artistCoverFolderRetrieverViewModelDelegate.sync(coverFolderRetriever)

        CoverFolderRetrieverState(
            coverFolderRetriever = coverFolderRetriever,
        )
    }.stateIn(
        scope = viewModelScope.plus(workDispatcher.dispatcher),
        started = SharingStarted.Eagerly,
        initialValue = CoverFolderRetrieverState(
            coverFolderRetriever = CoverFolderRetriever.default,
        )
    )
}
