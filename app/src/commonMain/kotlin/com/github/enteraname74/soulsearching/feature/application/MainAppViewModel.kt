package com.github.enteraname74.soulsearching.feature.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.composables.navigation.NavigationRowSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_settings
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_settings_filled
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.usecase.ShouldInformOfNewReleaseUseCase
import com.github.enteraname74.soulsearching.ext.filledIcon
import com.github.enteraname74.soulsearching.ext.isComingFromPlaylistDetails
import com.github.enteraname74.soulsearching.ext.outlinedIcon
import com.github.enteraname74.soulsearching.ext.text
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageDestination
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsDestination
import com.github.enteraname74.soulsearching.feature.tabmanager.TabManager
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainAppViewModel(
    shouldInformOfNewReleaseUseCase: ShouldInformOfNewReleaseUseCase,
    private val tabManager: TabManager,
    private val playerViewManager: PlayerViewManager,
    private val colorThemeManager: ColorThemeManager,
    private val navigator: Navigator,
) : ViewModel() {
    val state: StateFlow<MainAppState> = combine(
        tabManager.tabs,
        tabManager.currentPage,
        shouldInformOfNewReleaseUseCase()
    ) { tabs, currentPage, shouldShowNewVersionPin ->
        MainAppState(
            navigationRows = buildNavigationRows(
                shouldShowNewVersionPin = shouldShowNewVersionPin,
                currentPage = currentPage,
                tabs = tabs,
            )
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainAppState(),
        started = SharingStarted.Eagerly,
    )

    private fun buildNavigationRows(
        shouldShowNewVersionPin: Boolean,
        currentPage: ElementEnum?,
        tabs: List<ElementEnum>,
    ): List<NavigationRowSpec> = buildList {
        add(
            NavigationRowSpec(
                title = strings.settings,
                onClick = {
                    playerViewManager.minimiseIfPossible()
                    if (navigator.isComingFromPlaylistDetails()) {
                        colorThemeManager.removePlaylistTheme()
                    }
                    navigator.push(SettingsDestination)
                },
                filledIcon = CoreRes.drawable.ic_settings_filled,
                outlinedIcon = CoreRes.drawable.ic_settings,
                isSelected = navigator.currentRoute is SettingPage,
                isBadged = shouldShowNewVersionPin,
            )
        )
        tabs.forEachIndexed { index, tab ->

            val pageCheck: Boolean = (currentPage == null && index == 0) || (currentPage == tab)

            add(
                NavigationRowSpec(
                    title = tab.text(),
                    filledIcon = tab.filledIcon(),
                    outlinedIcon = tab.outlinedIcon(),
                    onClick = {
                        playerViewManager.minimiseIfPossible()
                        if (navigator.isComingFromPlaylistDetails()) {
                            colorThemeManager.removePlaylistTheme()
                        }
                        if (navigator.currentRoute != MainPageDestination) {
                            navigator.push(MainPageDestination)
                        }
                        tabManager.setCurrentPage(tab)
                    },
                    isSelected = (navigator.currentRoute is MainPageDestination) && pageCheck
                )
            )
        }
    }
}