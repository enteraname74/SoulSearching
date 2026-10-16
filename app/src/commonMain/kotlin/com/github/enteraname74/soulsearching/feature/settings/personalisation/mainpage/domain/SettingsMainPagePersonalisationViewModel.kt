package com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import kotlinx.coroutines.launch
import kotlin.math.max

class SettingsMainPagePersonalisationViewModel(
    private val settingsRepository: SettingsRepository,
    viewSettingsManager: ViewSettingsManager,
    workDispatcher: WorkDispatcher,
) : ViewModel() {
    val state: StateFlow<SettingsMainPagePersonalisationState> = combine(
        viewSettingsManager.visibleElements,
        settingsRepository.observeMainPage(),
    ) { elements, settings ->
        val savedInitial: ElementEnum? = ElementEnum.fromRaw(settings.initialTab)
        val selectableTabs = elements.toElementEnums()

        SettingsMainPagePersonalisationState.Data(
            elementsVisibility = elements,
            isUsingVerticalAccessBar = settings.isUsingVerticalAccessBar,
            selectableTabs = selectableTabs,
            initialTab = savedInitial?.takeIf { it in selectableTabs } ?: selectableTabs.first(),
        )
    }.stateIn(
        scope = viewModelScope.plus(workDispatcher.dispatcher),
        started = SharingStarted.Eagerly,
        initialValue = SettingsMainPagePersonalisationState.Loading
    )


    fun toggleQuickAccessVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        viewModelScope.launch { settingsRepository.setQuickAccessShown(!elementsVisibility.isQuickAccessShown) }
    }

    fun togglePlaylistsVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        viewModelScope.launch { settingsRepository.setPlaylistsShown(!elementsVisibility.arePlaylistsShown) }
    }

    fun toggleCollectionsVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        viewModelScope.launch { settingsRepository.setCollectionsShown(!elementsVisibility.areCollectionsShown) }
    }


    fun toggleAlbumsVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        viewModelScope.launch { settingsRepository.setAlbumsShown(!elementsVisibility.areAlbumsShown) }
    }

    fun toggleArtistsVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        viewModelScope.launch { settingsRepository.setArtistsShown(!elementsVisibility.areArtistsShown) }
    }

    fun toggleMusicFoldersVisibility() {
        val elementsVisibility =
            (state.value as? SettingsMainPagePersonalisationState.Data)?.elementsVisibility ?: return

        viewModelScope.launch { settingsRepository.setMusicsByFoldersShown(!elementsVisibility.areMusicFoldersShown) }
    }

    fun setShortcutAccessChoice(isVerticalAccessSelected: Boolean) {
        viewModelScope.launch { settingsRepository.setUsingVerticalAccessBar(isVerticalAccessSelected) }
    }

    fun setInitialTab(elementEnum: ElementEnum) {
        viewModelScope.launch { settingsRepository.setInitialTab(elementEnum.name) }
    }
}
