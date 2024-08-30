package com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

class SettingsMainPagePersonalisationViewModel(
    private val settings: SoulSearchingSettings,
    viewSettingsManager: ViewSettingsManager,
) : ScreenModel {
    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SettingsMainPagePersonalisationState> =
        viewSettingsManager.visibleElements.mapLatest { elements ->
            SettingsMainPagePersonalisationState.Data(
                elementsVisibility = elements,
            )
        }.stateIn(
            scope = screenModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = SettingsMainPagePersonalisationState.Loading
        )


    fun toggleQuickAccessVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        settings.setBoolean(
            key = SoulSearchingSettings.IS_QUICK_ACCESS_SHOWN,
            value = !elementsVisibility.isQuickAccessShown
        )
    }

    fun togglePlaylistsVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        settings.setBoolean(
            key = SoulSearchingSettings.IS_PLAYLISTS_SHOWN,
            value = !elementsVisibility.arePlaylistsShown
        )
    }


    fun toggleAlbumsVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        settings.setBoolean(
            key = SoulSearchingSettings.IS_ALBUMS_SHOWN,
            value = !elementsVisibility.areAlbumsShown
        )
    }

    fun toggleArtistsVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        settings.setBoolean(
            key = SoulSearchingSettings.IS_ARTISTS_SHOWN,
            value = !elementsVisibility.areArtistsShown
        )
    }

    fun toggleMusicFoldersVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        settings.setBoolean(
            key = SoulSearchingSettings.ARE_MUSICS_BY_FOLDERS_SHOWN,
            value = !elementsVisibility.areMusicFoldersShown
        )
    }
}