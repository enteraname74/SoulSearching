package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.artist

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

class SettingsArtistCoverMethodViewModel(
    settings: SoulSearchingSettings,
    private val artistCoverFolderRetrieverViewModelDelegate: ArtistCoverFolderRetrieverViewModelDelegate,
): ScreenModel, CoverFolderRetrieverActions by artistCoverFolderRetrieverViewModelDelegate {
    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<CoverFolderRetrieverState> = settings.getFlowOn(
        settingElement = SoulSearchingSettingsKeys.Cover.ARTIST_COVER_FOLDER_RETRIEVER
    ).mapLatest { coverFolderRetrieverState ->
        val deserialized: CoverFolderRetriever = artistCoverFolderRetrieverViewModelDelegate
            .deserializeCoverFolderRetriever(coverFolderRetrieverState)

        artistCoverFolderRetrieverViewModelDelegate.coverFolderRetriever = deserialized

        CoverFolderRetrieverState(
            coverFolderRetriever = deserialized,
        )
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = CoverFolderRetrieverState(
            coverFolderRetriever = CoverFolderRetriever.default,
        )
    )

    companion object {
        const val COVER_FILE_NAME_ID = "COVER_FILE_NAME"
    }
}