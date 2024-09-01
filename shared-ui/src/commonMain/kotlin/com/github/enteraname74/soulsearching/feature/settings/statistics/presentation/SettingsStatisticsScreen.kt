package com.github.enteraname74.soulsearching.feature.settings.statistics.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.statistics.domain.SettingsStatisticsState
import com.github.enteraname74.soulsearching.feature.settings.statistics.domain.SettingsStatisticsViewModel
import com.github.enteraname74.soulsearching.feature.settings.statistics.presentation.composable.settingsStatisticsSection

class SettingsStatisticsScreen: Screen {

    @Composable
    override fun Content() {
        val screenModel: SettingsStatisticsViewModel = getScreenModel()
        val state: SettingsStatisticsState by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        DataScreen(
            state = state,
            navigateBack = { navigator.pop() }
        )

    }

    @Composable
    private fun DataScreen(
        state: SettingsStatisticsState,
        navigateBack: () -> Unit,
    ) {
        SettingPage(
            navigateBack = navigateBack,
            title = strings.statisticsTitle,
            verticalPadding = 0.dp,
            contentPadding = PaddingValues(
                vertical = UiConstants.Spacing.veryLarge,
                horizontal = UiConstants.Spacing.medium,
            )
        ) {
            settingsStatisticsSection(
                title = strings.mostPlayedSongs,
                elements = state.mostListenedMusics,
            )
            settingsStatisticsSection(
                title = strings.mostPlayedArtists,
                elements = state.mostListenedArtists,
            )
            settingsStatisticsSection(
                title = strings.artistsWithMostSongs,
                elements = state.artistsWithMostSongs,
            )
            settingsStatisticsSection(
                title = strings.mostPlayedAlbums,
                elements = state.mostListenedAlbums,
            )
            settingsStatisticsSection(
                title = strings.mostPlayedPlaylists,
                elements = state.mostListenedPlaylists,
            )
        }
    }
}