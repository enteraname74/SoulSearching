package com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
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

        settings.set(
            key = SoulSearchingSettingsKeys.MainPage.IS_QUICK_ACCESS_SHOWN.key,
            value = !elementsVisibility.isQuickAccessShown
        )
    }

    fun togglePlaylistsVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        settings.set(
            key = SoulSearchingSettingsKeys.MainPage.IS_PLAYLISTS_SHOWN.key,
            value = !elementsVisibility.arePlaylistsShown
        )
    }


    fun toggleAlbumsVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        settings.set(
            key = SoulSearchingSettingsKeys.MainPage.IS_ALBUMS_SHOWN.key,
            value = !elementsVisibility.areAlbumsShown
        )
    }

    fun toggleArtistsVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        settings.set(
            key = SoulSearchingSettingsKeys.MainPage.IS_ARTISTS_SHOWN.key,
            value = !elementsVisibility.areArtistsShown
        )
    }

    fun toggleMusicFoldersVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        settings.set(
            key = SoulSearchingSettingsKeys.MainPage.ARE_MUSICS_BY_FOLDERS_SHOWN.key,
            value = !elementsVisibility.areMusicFoldersShown
        )
    }
}