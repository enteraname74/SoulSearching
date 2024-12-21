package com.github.enteraname74.soulsearching.feature.settings.personalisation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.House
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.ext.safePush
import com.github.enteraname74.soulsearching.feature.settings.SettingPage
import com.github.enteraname74.soulsearching.feature.settings.personalisation.mainpage.presentation.SettingsMainPagePersonalisationScreen
import com.github.enteraname74.soulsearching.feature.settings.personalisation.player.presentation.SettingsPlayerPersonalisationScreen
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

/**
 * Represent the view of the personalisation screen in the settings.
 */
class SettingsPersonalisationScreen : Screen, SettingPage {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SettingsPersonalisationScreenView(
            navigateBack = {
                navigator.pop()
            },
            onMainPageClick = {
                navigator.safePush(
                    SettingsMainPagePersonalisationScreen()
                )
            },
            onMusicPageClick = {
                navigator.safePush(
                    SettingsMusicViewPersonalisationScreen()
                )
            },
            onPlayerPageClick = {
                navigator.safePush(
                    SettingsPlayerPersonalisationScreen()
                )
            }
        )
    }

    @Composable
    private fun SettingsPersonalisationScreenView(
        navigateBack: () -> Unit,
        onMainPageClick: () -> Unit,
        onMusicPageClick: () -> Unit,
        onPlayerPageClick: () -> Unit
    ) {
        SettingPage(
            navigateBack = navigateBack,
            title = strings.personalizationTitle,
        ) {
            item {
                SoulMenuElement(
                    title = strings.mainPageTitle,
                    subTitle = strings.mainPageText,
                    icon = Icons.Rounded.House,
                    onClick = onMainPageClick
                )
            }
            item {
                SoulMenuElement(
                    title = strings.musics,
                    subTitle = strings.manageMusicsViewText,
                    icon = Icons.Rounded.MusicNote,
                    onClick = onMusicPageClick
                )
            }
            item {
                SoulMenuElement(
                    title = strings.managePlayerTitle,
                    subTitle = strings.managePlayerText,
                    icon = Icons.Rounded.PlayArrow,
                    onClick = onPlayerPageClick
                )
            }
        }
    }
}